package validator;

import java.util.Stack;
import parser.InvalidJsonException;
import parser.JsonParser;

/**
 * Implements the JsonParser interface.
 * This validates JSON input character-by-character and checks for valid JSON syntax.
 * This adapts from one parsing phase to the other depending on the current input.
 */
public class JsonValidator implements JsonParser {

  private static final String out1 = "Status:Empty";
  private static final String out2 = "Status:Valid";
  private static final String out3 = "Status:Incomplete";
  private static final String out4 = "Status:Invalid";

  private static final String inlPhs = "start";
  private static final String startObj = "object";
  private static final String startKey = "key";
  private static final String colon = "colon";
  private static final String startVal = "value";
  private static final String comma = "comma";
  private static final String startStr = "string";

  private String prePhs;
  private String preSts;
  private String preKey;
  private boolean inStr;
  private final Stack<Character> brk;

  /**
   * Constructs a new instance of the JsonValidator class.
   * Initializes all variables and a stack for tracking the brackets that are passed.
   */
  public JsonValidator() {
    brk = new Stack<>();
    prePhs = inlPhs;
    inStr = false;
    preKey = "";
    preSts = out1;
  }

  /**
   * It determines the appropriate parsing phase based on input.
   *
   * @param inpCh the character that is currently being processed
   * @return the updated status
   * @throws InvalidJsonException if the input character is not adhering to valid JSON syntax
   */
  @Override
  public JsonParser input(char inpCh) throws InvalidJsonException {
    if (!inStr) {
      if (Character.isWhitespace(inpCh)) {
        return this;
      }
    }

    switch (prePhs) {
      case inlPhs:
        inlStg(inpCh);
        break;
      case startObj:
        objStg(inpCh);
        break;
      case startKey:
        keyStg(inpCh);
        break;
      case colon:
        colonStg(inpCh);
        break;
      case startVal:
        valStg(inpCh);
        break;
      case startStr:
        strStg(inpCh);
        break;
      case comma:
        commaStg(inpCh);
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
   * @param inpCh the character that is being processed currently
   * @throws InvalidJsonException if the first character is not staring with curly brace
   */
  private void inlStg(char inpCh) throws InvalidJsonException {
    if (inpCh == '{') {
      brk.push(inpCh);
      prePhs = startObj;
    } else {
      preSts = out4;
      throw new InvalidJsonException("not starting with curly brace");
    }
  }

  /**
   * Handles the object phase of the input.
   *
   * @param inpCh the character that is currently being processed
   * @throws InvalidJsonException if an invalid character is encountered
   */
  private void objStg(char inpCh) throws InvalidJsonException {
    if (inpCh == '"') {
      prePhs = startKey;
      inStr = true;
    } else if (inpCh == '}') {
      brkChk('{');
    } else {
      preSts = out4;
      throw new InvalidJsonException("missing '\"' or '}'");
    }
  }

  /**
   * Handles the key phase of the input.
   *
   * @param inpCh the character that is being processed currently
   * @throws InvalidJsonException if the key is empty or contains invalid characters
   */
  private void keyStg(char inpCh) throws InvalidJsonException {
    if (inpCh == '"') {
      if (preKey.isEmpty()) {
        preSts = out4;
        throw new InvalidJsonException("key is empty");
      }
      inStr = false;
      preKey = "";
      prePhs = colon;
    } else {
      keyChk(inpCh);
      preKey += inpCh;
    }
  }

  /**
   * Checks if the characters in a key are adhering to JSON rules or not.
   *
   * @param inpCh the character that is being checked
   * @throws InvalidJsonException if the key contains invalid characters
   */
  private void keyChk(char inpCh) throws InvalidJsonException {
    if (preKey.isEmpty() && !Character.isLetter(inpCh)) {
      preSts = out4;
      throw new InvalidJsonException("key should start with letter");
    }
    if (!Character.isLetterOrDigit(inpCh)) {
      preSts = out4;
      throw new InvalidJsonException("key should have only letters and numbers");
    }
  }

  /**
   * Handles the colon phase of the input.
   *
   * @param inpCh the character that is currently being processed
   * @throws InvalidJsonException if the colon is missing at an expected place
   *                              according to the JSON syntax
   */
  private void colonStg(char inpCh) throws InvalidJsonException {
    if (inpCh == ':') {
      prePhs = startVal;
    } else {
      preSts = out4;
      throw new InvalidJsonException("missing colon");
    }
  }

  /**
   * Handles the value phase of the input.
   *
   * @param inpCh the character that is being processed currently
   * @throws InvalidJsonException if the value is not one of the object, string or array
   */
  private void valStg(char inpCh) throws InvalidJsonException {
    if (inpCh == '"') {
      prePhs = startStr;
      inStr = true;
    } else if (inpCh == '{') {
      brk.push(inpCh);
      prePhs = startObj;
    } else if (inpCh == '[') {
      brk.push(inpCh);
      prePhs = startVal;
    } else {
      preSts = out4;
      throw new InvalidJsonException("value is not one of the object, string or array");
    }
  }

  /**
   * Handles the string phase.
   *
   * @param inpCh the character being processed
   */
  private void strStg(char inpCh) {
    if (inpCh == '"') {
      inStr = false;
      prePhs = comma;
    }
  }

  /**
   * Handles the comma phase of the input.
   *
   * @param inpCh the character being processed
   * @throws InvalidJsonException if an invalid character is encountered during the comma phase
   */
  private void commaStg(char inpCh) throws InvalidJsonException {
    if (inpCh == ',') {
      if (brk.peek() == '{') {
        prePhs = startObj;
      } else {
        prePhs = startVal;
      }
    } else if (inpCh == ']') {
      brkChk('[');
      prePhs = comma;
    } else if (inpCh == '}') {
      brkChk('{');
    } else {
      preSts = out4;
      throw new InvalidJsonException("missing comma or closing bracket");
    }
  }

  /**
   * Checks if the form of the JSON brackets is correct.
   *
   * @param expCh the expected character
   * @throws InvalidJsonException if the brackets are improperly placed
   */
  private void brkChk(char expCh) throws InvalidJsonException {
    if (brk.isEmpty() || brk.pop() != expCh) {
      preSts = out4;
      throw new InvalidJsonException("improperly placed brackets");
    }
  }

  /**
   * Updates the current status of the process.
   * The status is set to either "Valid", "Incomplete" or "Invalid" according to the parsing done.
   */
  private void updSts() {
    if (preSts.equals(out4)) {
      return;
    }
    if (brk.isEmpty() && !prePhs.equals(inlPhs)) {
      preSts = out2;
    } else {
      preSts = out3;
    }
  }

  /**
   * Generates the status of the given input.
   *
   * @return the parsing status
   */
  @Override
  public String output() {
    return preSts;
  }
}
