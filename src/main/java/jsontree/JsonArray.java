package jsontree;

import java.util.ArrayList;
import java.util.List;

public class JsonArray extends IJsonArray {
  private final List<JsonNode> elements;

  public JsonArray() {
    this.elements = new ArrayList<>();
  }

  @Override
  public void add(JsonNode value) {
    elements.add(value);
  }

  @Override
  protected String prettyPrint(int level) {
    StringBuilder sb = new StringBuilder("[\n");

    if (!elements.isEmpty()) {
      for (int i = 0; i < elements.size(); i++) {
        sb.append(indent(level + 1))
                .append(elements.get(i).prettyPrint(level + 1));

        if (i < elements.size() - 1) {
          sb.append(",");
        }
        sb.append("\n");
      }
    }

    sb.append(indent(level)).append("]");
    return sb.toString();
  }
}