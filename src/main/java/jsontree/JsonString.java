package jsontree;

public class JsonString extends JsonNode {
  private final String value;

  public JsonString(String value) {
    this.value = value;
  }

  @Override
  public String prettyPrint() {
    return prettyPrint(0);
  }

  @Override
  protected String prettyPrint(int level) {
    return "\"" + value + "\"";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof JsonString)) return false;
    JsonString other = (JsonString) obj;
    return value.equals(other.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}