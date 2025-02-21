package jsontree;

/**
 * Abstract class representing a specification for a JSON array.
 * This class defines the necessary behavior for a JSON array node in a JSON structure.
 */
public abstract class IJsonArray extends JsonNode {

  /**
   * Appends a new element to this JSON array.
   * This method allows adding a new value to the array.
   *
   * @param value the {@link JsonNode} element to be added to the array.
   */
  public abstract void add(JsonNode value);
}
