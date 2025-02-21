package jsontree;

import parser.InvalidJsonException;
import parser.JsonParser;
import validator.JsonValidator;
import java.util.Stack;

/**
 * JsonTreeBuilder parses a JSON string character-by-character and constructs a JSON tree.
 * This builder handles various JSON elements such as objects, arrays, and strings.
 */
public class JsonTreeBuilder implements JsonParser<JsonNode> {

  private final JsonValidator validator;
  private final Stack<JsonNode> stack;
  private JsonNode root;
  private String currentKey;
  private boolean parsingString;
  private final StringBuilder currentValue;

  /**
   * Initializes the JsonTreeBuilder with a JsonValidator.
   * This constructor prepares the necessary state for JSON parsing.
   */
  public JsonTreeBuilder() {
    this.validator = new JsonValidator();
    this.stack = new Stack<>();
    this.currentValue = new StringBuilder();
    this.root = null;
    this.currentKey = null;
    this.parsingString = false;
  }

  /**
   * Processes a single character of JSON input and builds the tree.
   * Validates the character and handles different types of JSON data (string, object, array).
   *
   * @param c the character to process.
   * @return the JsonTreeBuilder instance for method chaining.
   * @throws InvalidJsonException if the input is invalid.
   */
  @Override
  public JsonParser<JsonNode> input(char c) throws InvalidJsonException {
    validator.input(c);
    if (Character.isWhitespace(c) && !parsingString) {
      return this;
    }

    if (parsingString) {
      if (c == '"') {
        parsingString = false;
        String str = currentValue.toString();
        if (!stack.isEmpty()) {
          if (stack.peek() instanceof JsonArray) {
            ((JsonArray) stack.peek()).add(new JsonString(str));
          } else if (stack.peek() instanceof JsonObject) {
            if (currentKey == null) {
              currentKey = str;
            } else {
              ((JsonObject) stack.peek()).add(currentKey, new JsonString(str));
              currentKey = null;
            }
          }
        } else {
          root = new JsonString(str);
        }
        currentValue.setLength(0);
      } else {
        currentValue.append(c);
      }
    } else {
      switch (c) {
        case '{':
          addNewContainer(new JsonObject());
          break;
        case '}':
          if (stack.isEmpty() || !(stack.peek() instanceof JsonObject)) {
            throw new InvalidJsonException("Mismatched closing brace");
          }
          stack.pop();
          break;
        case '[':
          addNewContainer(new JsonArray());
          break;
        case ']':
          if (stack.isEmpty() || !(stack.peek() instanceof JsonArray)) {
            throw new InvalidJsonException("Mismatched closing bracket");
          }
          stack.pop();
          break;
        case ':':
          // Colon separates key and value; no action needed.
          break;
        case ',':
          // Comma separates elements; no action needed.
          break;
        case '"':
          parsingString = true;
          currentValue.setLength(0);
          break;
        default:
          throw new InvalidJsonException("Unexpected character: " + c);
      }
    }
    return this;
  }

  /**
   * Helper method to add a new container (object or array) to the tree.
   * If a parent container exists, it adds the new container appropriately;
   * otherwise, it sets the new container as the root.
   * Finally, the new container is pushed onto the stack.
   *
   * @param container the new container (JsonObject or JsonArray)
   */
  private void addNewContainer(JsonNode container) {
    if (!stack.isEmpty()) {
      JsonNode top = stack.peek();
      if (top instanceof JsonObject && currentKey != null) {
        ((JsonObject) top).add(currentKey, container);
        currentKey = null;
      } else if (top instanceof JsonArray) {
        ((JsonArray) top).add(container);
      }
    } else {
      root = container;
    }
    stack.push(container);
  }

  /**
   * Returns the root of the parsed JSON tree if valid; otherwise, null.
   *
   * @return the root {@link JsonNode} representing the parsed JSON tree, or null if invalid.
   */
  @Override
  public JsonNode output() {
    return validator.output().equals("Status:Valid") ? root : null;
  }
}
