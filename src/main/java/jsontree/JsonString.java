package jsontree;

/**
 * Represents a JSON String node.
 * This class encapsulates a string value as part of a JSON structure.
 */
public class JsonString extends JsonNode {

  private final String value;

  /**
   * Constructs a JsonString node with the given value.
   *
   * @param value the string value to be encapsulated in this JSON node.
   */
  public JsonString(String value) {
    this.value = value;
  }

  /**
   * Returns the value encapsulated by this JsonString node.
   *
   * @return the string value of this JsonString node.
   */
  protected Object valueObtain() {
    return value;
  }

  /**
   * Returns the type of this JSON node, which is {@link typeOfNode#STRING}.
   *
   * @return the type of this node, which is always {@link typeOfNode#STRING} for JsonString.
   */
  protected typeOfNode typeObtain() {
    return typeOfNode.STRING;
  }
}
