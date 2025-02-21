package jsontree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap;

/**
 * Concrete class representing a JSON object.
 * This class implements the behavior for managing key-value pairs in a JSON object.
 * The keys are validated to ensure they follow a specific naming convention.
 */
public class JsonObject extends IJsonObject {

  // List of key-value pairs represented as Map.Entry to allow duplicate keys.
  private final List<Map.Entry<String, JsonNode>> entries = new ArrayList<>();

  /**
   * Adds a new key-value pair to the JSON object.
   * Validates the key to ensure it follows the specified naming convention (starts with a letter
   * and contains alphanumeric characters).
   * If the key is invalid, an {@link IllegalArgumentException} is thrown.
   *
   * @param key the key to be added to the JSON object.
   * @param value the {@link JsonNode} value associated with the key.
   * @throws IllegalArgumentException if the key does not follow the naming convention.
   */
  @Override
  public void add(String key, JsonNode value) {
    if (!key.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
      throw new IllegalArgumentException(key + " is not a valid key");
    }
    // Add a new key-value pair to the list using AbstractMap.SimpleEntry
    entries.add(new AbstractMap.SimpleEntry<>(key, value));
  }

  /**
   * Returns the type of the JSON node, which is {@link typeOfNode#OBJECT} for this class.
   *
   * @return {@link typeOfNode#OBJECT}.
   */
  protected typeOfNode typeObtain() {
    return typeOfNode.OBJECT;
  }

  /**
   * Returns the value of the JSON object, which is a list of key-value pairs.
   *
   * @return a list of {@link Map.Entry} containing the key-value pairs.
   */
  protected Object valueObtain() {
    return entries;
  }
}
