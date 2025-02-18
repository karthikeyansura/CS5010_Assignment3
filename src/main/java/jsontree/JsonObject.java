package jsontree;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObject extends IJsonObject {
  private final Map<String, JsonNode> entries;

  public JsonObject() {
    this.entries = new LinkedHashMap<>();
  }

  @Override
  public void add(String key, JsonNode value) {
    if (!isValidKey(key)) {
      throw new IllegalArgumentException("Invalid key format");
    }
    entries.put(key, value);
  }

  private boolean isValidKey(String key) {
    if (key == null || key.isEmpty() || !Character.isLetter(key.charAt(0))) {
      return false;
    }
    return key.chars().allMatch(ch -> Character.isLetterOrDigit(ch));
  }

  @Override
  protected String prettyPrint(int level) {
    StringBuilder sb = new StringBuilder("{\n");

    if (!entries.isEmpty()) {
      int count = 0;
      for (Map.Entry<String, JsonNode> entry : entries.entrySet()) {
        sb.append(indent(level + 1))
                .append("\"").append(entry.getKey()).append("\":")
                .append(entry.getValue().prettyPrint(level + 1));

        if (count < entries.size() - 1) {
          sb.append(",");
        }
        sb.append("\n");
        count++;
      }
    }

    sb.append(indent(level)).append("}");
    return sb.toString();
  }
}