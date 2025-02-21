package jsontree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract class representing a JSON node. This class provides methods for comparing JSON nodes,
 * calculating hash codes, and pretty printing nodes.
 */
public abstract class JsonNode {

  /**
   * Enum representing the type of the JSON node.
   */
  protected enum typeOfNode {
    ARRAY, OBJECT, STRING
  }

  /**
   * Abstract method that returns the type of the JSON node.
   *
   * @return the type of the JSON node (ARRAY, OBJECT, or STRING).
   *
   */
  protected abstract typeOfNode typeObtain();

  /**
   * Abstract method that returns the value of the JSON node.
   *
   * @return the value of the JSON node.
   */
  protected abstract Object valueObtain();


  /**
   * Compares this JSON node with another object for equality. Two nodes are considered equal
   * if they have the same type and value.
   *
   * @param o the object to compare this JSON node with.
   * @return true if the JSON nodes are equal, false otherwise.
   */
  @Override
  @SuppressWarnings("unchecked")
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof JsonNode)) {
      return false;
    }
    JsonNode that = (JsonNode) o;

    if (this.typeObtain() != that.typeObtain()) {
      return false;
    }

    if (this.typeObtain() == typeOfNode.STRING) {
      return Objects.equals(this.valueObtain(), that.valueObtain());
    } else if (this.typeObtain() == typeOfNode.ARRAY) {
      return compareArr((List<JsonNode>) this.valueObtain(),
                          (List<JsonNode>) that.valueObtain());
    } else if (this.typeObtain() == typeOfNode.OBJECT) {
      return compareObj((List<Map.Entry<String, JsonNode>>) this.valueObtain(),
              (List<Map.Entry<String, JsonNode>>) that.valueObtain());
    } else {
      throw new IllegalStateException("Unexpected value: " + this.typeObtain());
    }
  }

  /**
   * Generates a hash code for this JSON node.
   *
   * @return the hash code for this JSON node.
   */
  @Override
  @SuppressWarnings("unchecked")
  public final int hashCode() {
    if (typeObtain() == typeOfNode.OBJECT) {
      return Objects.hash(typeObtain(),
              listOfMapToMap((List<Map.Entry<String, JsonNode>>) valueObtain()));
    }
    return Objects.hash(typeObtain(), valueObtain());
  }

  /**
   * Compares two JSON objects for equality.
   *
   * @param object1 the first object.
   * @param object2 the second object.
   * @return true if the objects are equal, false otherwise.
   */
  private boolean compareObj(List<Map.Entry<String, JsonNode>> object1, List<Map.Entry<String,
                             JsonNode>> object2) {
    return listOfMapToMap(object1).equals(listOfMapToMap(object2));
  }

  /**
   * Compares two JSON arrays.
   *
   * @param array1 the first array.
   * @param array2 the second array.
   * @return true if the arrays are equal, false otherwise.
   */
  private boolean compareArr(List<JsonNode> array1, List<JsonNode> array2) {
    return array1.equals(array2);
  }

  /**
   * Converts a list of map to a map for comparison.
   *
   * @param storedEntries the list of map entries.
   * @return a map with key-value pairs.
   */
  private Map<String, List<JsonNode>> listOfMapToMap(List<Map.Entry<String,
                                                     JsonNode>> storedEntries) {
    Map<String, List<JsonNode>> map = new HashMap<>();
    for (Map.Entry<String, JsonNode> entry : storedEntries) {
      map.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
    }
    for (List<JsonNode> values : map.values()) {
      values.sort(Comparator.comparing(JsonNode::hashCode));
    }
    return map;
  }

  /**
   * Pretty prints this JSON node.
   *
   * @return a pretty printed string representation of this JSON node.
   */
  public final String prettyPrint() {
    return prettyPrint(0);
  }

  /**
   * Pretty prints this JSON node with the specified indentation level.
   *
   * @param space the current indentation level.
   * @return a pretty-printed string representation of this JSON node.
   */
  protected final String prettyPrint(int space) {
    String indentStr = "  ".repeat(space);
    switch (typeObtain()) {
      case STRING:
        return prettyPrintOfStr();
      case ARRAY:
        return prettyPrintOfArr(space, indentStr);
      case OBJECT:
        return prettyPrintOfObject(space, indentStr);
      default:
        throw new IllegalStateException("Unexpected node type: " + typeObtain());
    }
  }


  /**
   * Pretty prints a JSON object.
   *
   * @param space the current indentation level.
   * @param spaceStr the string representing the indentation.
   * @return a pretty-printed string representation of the object.
   */
  @SuppressWarnings("unchecked")
  private String prettyPrintOfObject(int space, String spaceStr) {
    List<Map.Entry<String, JsonNode>> entries = (List<Map.Entry<String, JsonNode>>) valueObtain();
    StringBuilder sb = new StringBuilder("{\n");
    for (Map.Entry<String, JsonNode> entry : entries) {
      sb.append(spaceStr).append("  ")
              .append("\"").append(entry.getKey()).append("\":")
              .append(prettyPrintValue(entry.getValue(), space + 1));
      if (entries.indexOf(entry) < entries.size() - 1) {
        sb.append(",");
      }
      sb.append("\n");
    }
    return sb.append("  ".repeat(space)).append("}").toString();
  }

  private String prettyPrintValue(JsonNode value, int indent) {
    return (value instanceof JsonObject || value instanceof JsonArray)
            ? "\n" + "  ".repeat(indent) + value.prettyPrint(indent)
            : value.prettyPrint(indent);
  }

  /**
   * Pretty prints a string value.
   *
   * @return the string value wrapped in quotes.
   */
  private String prettyPrintOfStr() {
    return "\"" + valueObtain() + "\"";
  }

  /**
   * Pretty prints a JSON array.
   *
   * @param space the current indentation level.
   *
   * @param spaceStr the string representing the indentation.
   * @return a pretty-printed string representation of the array.
   */
  @SuppressWarnings("unchecked")
  private String prettyPrintOfArr(int space, String spaceStr) {
    List<JsonNode> elements = (List<JsonNode>) valueObtain();
    StringBuilder sb = new StringBuilder("[\n");
    for (int i = 0; i < elements.size(); i++) {
      sb.append(spaceStr).append("  ").append(elements.get(i).prettyPrint(space + 1));
      if (i < elements.size() - 1) {
        sb.append(",");
      }
      sb.append("\n");
    }
    return sb.append("  ".repeat(space)).append("]").toString();
  }
}
