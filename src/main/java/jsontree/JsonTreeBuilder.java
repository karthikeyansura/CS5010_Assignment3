package jsontree;

import parser.InvalidJsonException;
import parser.JsonParser;

import java.util.Stack;

public class JsonTreeBuilder implements JsonParser<JsonNode> {
  private enum State {
    START, OBJECT, KEY, COLON, VALUE, STRING, COMMA, ARRAY
  }

  private State currentState;
  private final Stack<Object> parseStack;  // Stack of JsonNode and State
  private StringBuilder currentString;
  private String currentKey;
  private boolean inString;
  private JsonNode root;
  private boolean isValid;

  public JsonTreeBuilder() {
    parseStack = new Stack<>();
    currentString = new StringBuilder();
    currentState = State.START;
    inString = false;
    isValid = true;
  }

  @Override
  public JsonParser<JsonNode> input(char c) throws InvalidJsonException {
    if (!isValid) {
      throw new InvalidJsonException("Parser is in invalid state");
    }

    if (!inString && Character.isWhitespace(c)) {
      return this;
    }

    try {
      switch (currentState) {
        case START:
          handleStartState(c);
          break;
        case OBJECT:
          handleObjectState(c);
          break;
        case KEY:
          handleKeyState(c);
          break;
        case COLON:
          handleColonState(c);
          break;
        case VALUE:
          handleValueState(c);
          break;
        case STRING:
          handleStringState(c);
          break;
        case COMMA:
          handleCommaState(c);
          break;
        case ARRAY:
          handleArrayState(c);
          break;
      }
    } catch (Exception e) {
      isValid = false;
      throw new InvalidJsonException(e.getMessage());
    }

    return this;
  }

  private void handleStartState(char c) throws InvalidJsonException {
    if (c == '{') {
      JsonObject obj = new JsonObject();
      parseStack.push(obj);
      currentState = State.OBJECT;
      root = obj;
    } else {
      throw new InvalidJsonException("JSON must start with {");
    }
  }

  private void handleObjectState(char c) throws InvalidJsonException {
    if (c == '"') {
      currentState = State.KEY;
      inString = true;
      currentString = new StringBuilder();
    } else if (c == '}') {
      if (parseStack.size() > 1) {
        JsonNode node = (JsonNode) parseStack.pop();
        handleCompletedNode(node);
      }
      currentState = State.COMMA;
    } else {
      throw new InvalidJsonException("Expected \" or }");
    }
  }

  private void handleKeyState(char c) throws InvalidJsonException {
    if (c == '"' && inString) {
      currentKey = currentString.toString();
      if (!isValidKey(currentKey)) {
        throw new InvalidJsonException("Invalid key format");
      }
      inString = false;
      currentState = State.COLON;
    } else {
      currentString.append(c);
    }
  }

  private boolean isValidKey(String key) {
    if (key.isEmpty() || !Character.isLetter(key.charAt(0))) {
      return false;
    }
    return key.chars().allMatch(ch -> Character.isLetterOrDigit(ch));
  }

  private void handleColonState(char c) throws InvalidJsonException {
    if (c == ':') {
      currentState = State.VALUE;
    } else {
      throw new InvalidJsonException("Expected :");
    }
  }

  private void handleValueState(char c) throws InvalidJsonException {
    if (c == '"') {
      currentState = State.STRING;
      inString = true;
      currentString = new StringBuilder();
    } else if (c == '{') {
      JsonObject obj = new JsonObject();
      parseStack.push(currentState);
      parseStack.push(currentKey);
      parseStack.push(obj);
      currentState = State.OBJECT;
    } else if (c == '[') {
      JsonArray arr = new JsonArray();
      parseStack.push(currentState);
      parseStack.push(currentKey);
      parseStack.push(arr);
      currentState = State.ARRAY;
    } else {
      throw new InvalidJsonException("Invalid value start");
    }
  }

  private void handleStringState(char c) {
    if (c == '"') {
      JsonString str = new JsonString(currentString.toString());
      handleCompletedNode(str);
      inString = false;
      currentState = State.COMMA;
    } else {
      currentString.append(c);
    }
  }

  private void handleCommaState(char c) throws InvalidJsonException {
    if (c == ',') {
      if (parseStack.peek() instanceof JsonObject) {
        currentState = State.OBJECT;
      } else if (parseStack.peek() instanceof JsonArray) {
        currentState = State.ARRAY;
      }
    } else if (c == '}' && parseStack.peek() instanceof JsonObject) {
      JsonNode node = (JsonNode) parseStack.pop();
      if (!parseStack.isEmpty()) {
        handleCompletedNode(node);
      }
    } else if (c == ']' && parseStack.peek() instanceof JsonArray) {
      JsonNode node = (JsonNode) parseStack.pop();
      if (!parseStack.isEmpty()) {
        handleCompletedNode(node);
      }
    } else {
      throw new InvalidJsonException("Expected , or closing bracket");
    }
  }

  private void handleArrayState(char c) throws InvalidJsonException {
    if (c == '"') {
      currentState = State.STRING;
      inString = true;
      currentString = new StringBuilder();
    } else if (c == '{') {
      JsonObject obj = new JsonObject();
      parseStack.push(currentState);
      parseStack.push(obj);
      currentState = State.OBJECT;
    } else if (c == '[') {
      JsonArray arr = new JsonArray();
      parseStack.push(currentState);
      parseStack.push(arr);
      currentState = State.ARRAY;
    } else if (c == ']') {
      JsonNode node = (JsonNode) parseStack.pop();
      if (!parseStack.isEmpty()) {
        handleCompletedNode(node);
      }
      currentState = State.COMMA;
    } else {
      throw new InvalidJsonException("Invalid array element");
    }
  }

  private void handleCompletedNode(JsonNode node) {
    if (!parseStack.isEmpty()) {
      Object parent = parseStack.peek();
      if (parent instanceof JsonArray) {
        ((JsonArray) parent).add(node);
      } else if (parent instanceof JsonObject) {
        Object keyObj = parseStack.pop();
        if (keyObj instanceof String) {
          String key = (String) keyObj;
          if (parseStack.peek() instanceof JsonObject) {
            ((JsonObject) parseStack.peek()).add(key, node);
          }
        }
      }
    }
  }

  @Override
  public JsonNode output() {
    // Return null for any incomplete or invalid JSON
    if (!isValid || !parseStack.isEmpty() || currentState != State.COMMA) {
      return null;
    }
    return root;
  }
}