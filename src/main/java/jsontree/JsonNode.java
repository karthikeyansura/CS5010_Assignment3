package jsontree;

public abstract class JsonNode {
  public String prettyPrint() {
    return prettyPrint(0);
  }

  protected String indent(int level) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < level; i++) {
      sb.append("  ");  // Two spaces per level
    }
    return sb.toString();
  }

  protected abstract String prettyPrint(int level);
}