package jsontree;

/**
 * Abstract class representing a specification for a JSON object.
 * This class defines the common structure and behavior that concrete implementations of
 * a JSON object must follow.
 * The key-value pairs in the object are expected to be managed by a subclass.
 */
public abstract class IJsonObject extends JsonNode {

  /**
   * Adds a new key-value pair to the JSON object.
   * The method is abstract and should be implemented by concrete subclasses to
   * define the actual behavior of adding a key-value pair to the object.
   * The key should be a valid string, typically following certain naming conventions.
   *
   * @param key the key to be added to the JSON object.
   * @param value the {@link JsonNode} value to be associated with the key.
   */
  public abstract void add(String key, JsonNode value);
}
