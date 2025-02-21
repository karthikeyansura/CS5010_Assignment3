package validator;

import java.util.Stack;
import parser.InvalidJsonException;
import parser.JsonParser;

/**
 * Implements the JsonParser interface.
 * This validates JSON input character-by-character and checks for valid JSON syntax.
 * This adapts from one parsing phase to the other depending on the current input.
 */
public class JsonValidator implements JsonParser<String> {

  private static final String output1 = "Status:Empty";
  private static final String output2 = "Status:Valid";
  private static final String output3 = "Status:Incomplete";
  private static final String output4 = "Status:Invalid";

  private static final String startPhase = "start";
  private static final String objectPhase = "object";
  private static final String keyPhase = "key";
  private static final String colon = "colon";
  private static final String valuePhase = "value";
  private static final String comma = "comma";
  private static final String startString = "string";

  private String initialPhase;
  private String preStatus;
  private String preKey;
  private boolean inString;
  private final Stack<Character> bracketContainer;

  /**
   * Constructs a new instance of the JsonValidator class.
   * Initializes all variables and a stack for tracking the brackets that are passed.
   */
  public JsonValidator() {
    bracketContainer = new Stack<>();
    initialPhase = startPhase;
    inString = false;
    preKey = "";
    preStatus = output1;
  }

  /**
   * It determines the appropriate parsing phase based on input.
   *
   * @param inCharacter the character that is currently being processed
   * @return the updated status
   * @throws InvalidJsonException if the input character is not adhering to valid JSON syntax
   */
  @Override
  public JsonParser<String> input(char inCharacter) throws InvalidJsonException {
    if (!inString) {
      if (Character.isWhitespace(inCharacter)) {

        return this;
      }
    }

    switch (initialPhase) {
      case startPhase:
        inlStg(inCharacter);
        break;
      case objectPhase:
        objStg(inCharacter);
        break;
      case keyPhase:
        keyStg(inCharacter);
        break;
      case colon:
        colonStg(inCharacter);
        break;
      case valuePhase:
        valStg(inCharacter);
        break;
      case startString:
        strStg(inCharacter);
        break;
      case comma:
        commaStg(inCharacter);
        break;
      default:
        // Nothing to be done just for completeness
        break;
    }

    updSts();
    return this;
  }

  /**
   * Handles the initial string phase of the input.
   *
   * @param inCharacter the character that is being processed currently
   * @throws InvalidJsonException if the first character is not staring with curly brace
   */
  private void inlStg(char inCharacter) throws InvalidJsonException {
    if (inCharacter == '{') {
      bracketContainer.push(inCharacter);

      initialPhase = objectPhase;
    } else {
      preStatus = output4;
      throw new InvalidJsonException("not starting with curly brace");
    }
  }

  /**
   * Handles the object phase of the input.
   *
   * @param inCharacter the character that is currently being processed
   * @throws InvalidJsonException if an invalid character is encountered
   */
  private void objStg(char inCharacter) throws InvalidJsonException {
    if (inCharacter == '"') {
      initialPhase = keyPhase;
      inString = true;
    } else if (inCharacter == '}') {
      brkChk('{');
    } else {
      preStatus = output4;
      throw new InvalidJsonException("missing '\"' or '}'");
    }
  }

  /**
   * Handles the key phase of the input.
   *
   * @param inCharacter the character that is being processed currently
   * @throws InvalidJsonException if the key is empty or contains invalid characters
   */
  private void keyStg(char inCharacter) throws InvalidJsonException {
    if (inCharacter == '"') {
      if (preKey.isEmpty()) {
        preStatus = output4;
        throw new InvalidJsonException("key is empty");
      }
      inString = false;
      preKey = "";
      initialPhase = colon;
    } else {
      keyChk(inCharacter);
      preKey += inCharacter;
    }
  }

  /**
   * Checks if the characters in a key are adhering to JSON rules or not.
   *
   * @param inCharacter the character that is being checked
   * @throws InvalidJsonException if the key contains invalid characters
   */
  private void keyChk(char inCharacter) throws InvalidJsonException {
    if (preKey.isEmpty() && !Character.isLetter(inCharacter)) {
      preStatus = output4;
      throw new InvalidJsonException("key should start with letter");
    }
    if (!Character.isLetterOrDigit(inCharacter)) {
      preStatus = output4;
      throw new InvalidJsonException("key should have only letters and numbers");
    }
  }

  /**
   * Handles the colon phase of the input.
   *
   * @param inCharacter the character that is currently being processed
   * @throws InvalidJsonException if the colon is missing at an expected place
   *                              according to the JSON syntax
   */
  private void colonStg(char inCharacter) throws InvalidJsonException {
    if (inCharacter == ':') {
      initialPhase = valuePhase;
    } else {
      preStatus = output4;
      throw new InvalidJsonException("missing colon");
    }
  }

  /**
   * Handles the value phase of the input.
   *
   * @param inCharacter the character that is being processed currently
   * @throws InvalidJsonException if the value is not one of the object, string or array
   */
  private void valStg(char inCharacter) throws InvalidJsonException {
    if (inCharacter == '"') {
      initialPhase = startString;
      inString = true;
    } else if (inCharacter == '{') {
      bracketContainer.push(inCharacter);
      initialPhase = objectPhase;
    } else if (inCharacter == '[') {
      bracketContainer.push(inCharacter);
      initialPhase = valuePhase;
    } else {
      preStatus = output4;
      throw new InvalidJsonException("value is not one of the object, string or array");
    }
  }

  /**
   * Handles the string phase.
   *
   * @param inCharacter the character being processed
   */
  private void strStg(char inCharacter) {
    if (inCharacter == '"') {
      inString = false;
      initialPhase = comma;
    }
  }

  /**
   * Handles the comma phase of the input.
   *
   * @param inCharacter the character being processed
   * @throws InvalidJsonException if an invalid character is encountered during the comma phase
   */
  private void commaStg(char inCharacter) throws InvalidJsonException {
    if (inCharacter == ',') {
      if (bracketContainer.peek() == '{') {
        initialPhase = objectPhase;
      } else {
        initialPhase = valuePhase;
      }
    } else if (inCharacter == ']') {
      brkChk('[');
      initialPhase = comma;
    } else if (inCharacter == '}') {
      brkChk('{');
    } else {
      preStatus = output4;
      throw new InvalidJsonException("missing comma or closing bracket");
    }
  }

  /**
   * Checks if the form of the JSON brackets is correct.
   *
   * @param expectedCharacter the expected character
   * @throws InvalidJsonException if the brackets are improperly placed
   */
  private void brkChk(char expectedCharacter) throws InvalidJsonException {
    if (bracketContainer.isEmpty() || bracketContainer.pop() != expectedCharacter) {
      preStatus = output4;
      throw new InvalidJsonException("improperly placed brackets");
    }
  }

  /**
   * Updates the current status of the process.
   * The status is set to either "Valid", "Incomplete" or "Invalid" according to the parsing done.
   */
  private void updSts() {
    if (preStatus.equals(output4)) {
      return;
    }
    if (bracketContainer.isEmpty() && !initialPhase.equals(startPhase)) {
      preStatus = output2;
    } else {
      preStatus = output3;
    }
  }

  /**
   * Generates the status of the given input.
   *
   * @return the parsing status
   */
  @Override
  public String output() {
    return preStatus;
  }
}
