package jsontree;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the {@link IJsonArray} class representing a JSON array.
 * This class provides the functionality for
 * managing and manipulating a list of {@link JsonNode} elements.
 */
public class JsonArray extends IJsonArray {

  /** The list of elements in the JSON array. */
  private final List<JsonNode> components = new ArrayList<>();

  /**
   * Adds a new element to the JSON array.
   * This method appends the provided {@link JsonNode} value to the list of elements.
   *
   * @param value the {@link JsonNode} element to be added to the array.
   */
  @Override
  public void add(JsonNode value) {
    components.add(value);
  }

  /**
   * Returns the type of the node, which is {@link typeOfNode#ARRAY} for a JSON array.
   *
   * @return the type of the node (ARRAY).
   */
  protected typeOfNode typeObtain() {
    return typeOfNode.ARRAY;
  }

  /**
   * Returns the value of the JSON array, which is a list of {@link JsonNode} elements.
   *
   * @return the list of elements in the JSON array.
   */
  protected Object valueObtain() {
    return components;
  }
}
