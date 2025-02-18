package jsontree;

public abstract class IJsonObject extends JsonNode {
  public abstract void add(String key, JsonNode value);
}