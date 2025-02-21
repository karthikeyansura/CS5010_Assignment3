package jsontree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for various JSON node operations.
 */
public class JsonNodeTest {

  /**
   * Test case to verify complex JSON structure with nested objects and multiple key-value pairs.
   */
  @Test
  public void testComplexStructure() {
    JsonObject root = new JsonObject();
    JsonObject time = new JsonObject();
    JsonArray students = new JsonArray();
    JsonObject status = new JsonObject();
    time.add("semester", new JsonString("spring 2 0 2 5"));
    time.add("year", new JsonString("2024"));
    root.add("name", new JsonString("cs5010"));
    root.add("status", status);
    root.add("time", time);
    root.add("students", students);
    root.add("name", new JsonString("cs5011"));
    String expected = "{\n"
            + "  \"name\":\"cs5010\",\n"
            + "  \"status\":\n  {\n  },\n"
            + "  \"time\":\n  {\n"
            + "    \"semester\":\"spring 2 0 2 5\",\n"
            + "    \"year\":\"2024\"\n"
            + "  },\n"
            + "  \"students\":\n  [\n"
            + "  ],\n"
            + "  \"name\":\"cs5011\"\n"
            + "}";
    assertEquals(expected, root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of a single string JSON node.
   * Input: "a".
   */
  @Test
  public void testOnlyStringPrettyPrint() {
    JsonString root = new JsonString("a");
    assertEquals("\"a\"", root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of an empty string JSON node.
   * Input: "" (empty string).
   */
  @Test
  public void testEmptyStringPrettyPrint() {
    JsonString root = new JsonString("");
    assertEquals("\"\"", root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of an empty array.
   * Input: [] (empty array).
   */
  @Test
  public void testArrayEmpty() {
    JsonArray root = new JsonArray();
    assertEquals("[\n]", root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of an array with a single element.
   * Input: ["a"].
   */
  @Test
  public void testArrayWithOnlySingleElement() {
    JsonArray root = new JsonArray();
    root.add(new JsonString("a"));
    assertEquals("[\n  \"a\"\n]", root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of an array with multiple elements.
   * Input: ["a", "b"].
   */
  @Test
  public void testArrayWithMultipleElements() {
    JsonArray root = new JsonArray();
    root.add(new JsonString("a"));
    root.add(new JsonString("b"));
    assertEquals("[\n  \"a\",\n  \"b\"\n]", root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of an empty object.
   * Input: {} (empty object).
   */
  @Test
  public void testEmptyObject() {
    JsonObject root = new JsonObject();
    assertEquals("{\n}", root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of an object with a single key-value pair.
   * Input: {"a": "b"}.
   */
  @Test
  public void testSingleKeyValue() {
    JsonObject root = new JsonObject();
    root.add("a", new JsonString("b"));
    assertEquals("{\n  \"a\":\"b\"\n}", root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of an object with multiple key-value pairs.
   * Input: {"a": "b", "x": "y"}.
   */
  @Test
  public void testMultipleKeyValue() {
    JsonObject root = new JsonObject();
    root.add("a", new JsonString("b"));
    root.add("x", new JsonString("y"));
    assertEquals("{\n  \"a\":\"b\",\n  \"x\":\"y\"\n}", root.prettyPrint());
  }

  /**
   * Test case to verify the pretty-printing of a nested JSON object.
   * Input: {"a": {"insideKey": "insideValue"}}.
   */
  @Test
  public void testNestedObject() {
    JsonObject root1 = new JsonObject();
    JsonObject root2 = new JsonObject();
    root2.add("insideKey", new JsonString("insideValue"));
    root1.add("a", root2);
    assertEquals("{\n  \"a\":\n  {\n    \"insideKey\":\"insideValue\"\n  }\n}",
            root1.prettyPrint());
  }

  /**
   * Test case to check if adding a key starting with a number, raises an exception.
   * Input: {"1a": "val"}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testKeyStartsWithNumber() {
    JsonObject root = new JsonObject();
    root.add("1a", new JsonString("val"));
  }

  /**
   * Test case to check if adding a key with special characters raises an exception.
   * Input: {"a$": "jhn"}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testKeyWithSpecialCharacters() {
    JsonObject root = new JsonObject();
    root.add("a$", new JsonString("jhn"));
  }

  /**
   * Test case to verify the addition of a valid key with numbers in its name.
   * Input: {"b1": "a b"}.
   */
  @Test
  public void testValidKeyWithNumbers() {
    JsonObject root = new JsonObject();
    root.add("b1", new JsonString("a b"));
    assertTrue(root.prettyPrint().contains("\"b1\":"));
  }

  /**
   * Test case to verify the handling of an object inside an array.
   * Input: ["c", {"ad": "bc"}].
   */
  @Test
  public void testObjectInsideArray() {
    JsonArray root1 = new JsonArray();
    root1.add(new JsonString("c"));
    JsonObject root2 = new JsonObject();
    root2.add("ad", new JsonString("bc"));
    root1.add(root2);
    String expected = "[\n"
            + "  \"c\",\n"
            + "  {\n"
            + "    \"ad\":\"bc\"\n"
            + "  }\n"
            + "]";
    assertEquals(expected, root1.prettyPrint());
  }

  /**
   * Test case to check the equivalency of JSON objects with keys in different order.
   */
  @Test
  public void testEquivalencyWithDifferentKeyValueOrder() {
    JsonObject obj1 = new JsonObject();
    JsonObject html1 = new JsonObject();
    JsonArray head1 = new JsonArray();
    head1.add(new JsonString("This"));
    head1.add(new JsonString("is"));
    head1.add(new JsonString("a"));
    head1.add(new JsonString("header"));
    html1.add("head", head1);
    html1.add("tail", new JsonObject());
    obj1.add("html", html1);
    JsonObject obj2 = new JsonObject();
    JsonObject html2 = new JsonObject();
    JsonArray head2 = new JsonArray();
    head2.add(new JsonString("This"));
    head2.add(new JsonString("is"));
    head2.add(new JsonString("a"));
    head2.add(new JsonString("header"));
    html2.add("tail", new JsonObject());
    html2.add("head", head2);
    obj2.add("html", html2);
    assertEquals(obj1, obj2);
  }

  /**
   * Test case to check equivalency of objects with duplicate keys.
   * Input: {"key1": "value1", "key2": "value2"}.
   */
  @Test
  public void testEquivalencyWithDuplicateKeys() {
    JsonObject root1 = new JsonObject();
    root1.add("key2", new JsonString("value2"));
    root1.add("key1", new JsonString("value1"));
    JsonObject root2 = new JsonObject();
    root2.add("key1", new JsonString("value1"));
    root2.add("key2", new JsonString("value2"));
    assertEquals(root1, root2);
  }

  /**
   * Test case to check equivalency of empty arrays.
   */
  @Test
  public void testEmptyArraysEquivalency() {
    JsonArray root1 = new JsonArray();
    JsonArray root2 = new JsonArray();
    assertEquals(root1, root2);
  }

  /**
   * Test case to check non-equivalency of arrays with different order of elements.
   */
  @Test
  public void testArraysWithDifferentOrder() {
    JsonArray root1 = new JsonArray();
    root1.add(new JsonString("one"));
    root1.add(new JsonString("two"));
    root1.add(new JsonString("three"));
    JsonArray root2 = new JsonArray();
    root2.add(new JsonString("one"));
    root2.add(new JsonString("three"));
    root2.add(new JsonString("two"));
    assertNotEquals(root1, root2);
  }

  /**
   * Test case to check equivalency of objects and arrays with different structures.
   */
  @Test
  public void testEquivalencyWithObjectsAndArrays() {
    JsonObject root1 = new JsonObject();
    root1.add("key", new JsonString("value"));
    JsonArray root2 = new JsonArray();
    root2.add(new JsonString("key"));
    root2.add(new JsonString("value"));
    assertNotEquals(root1, root2);
  }

  /**
   * Test case to check equivalency of empty objects.
   */
  @Test
  public void testEmptyObjectsEquivalency() {
    JsonObject root1 = new JsonObject();
    JsonObject root2 = new JsonObject();
    assertEquals(root1, root2);
  }

  /**
   * Test case to verify inequality between two JsonString objects with different values.
   */
  @Test
  public void testJsonStringInequality() {
    JsonString root1 = new JsonString(" ");
    JsonString root2 = new JsonString("b");
    assertNotEquals(root1, root2);
  }

  /**
   * Test case to verify equality between two JsonString objects with the same value.
   */
  @Test
  public void testJsonStringEquality() {
    JsonString root1 = new JsonString(" ");
    JsonString root2 = new JsonString(" ");
    assertEquals(root1, root2);
  }

  /**
   * Test case to check non-equivalency between objects
   * with arrays having different order of elements.
   */
  @Test
  public void testNonEquivalencyWithDifferentArrayValueOrder() {
    JsonObject obj1 = new JsonObject();
    JsonObject html1 = new JsonObject();
    JsonArray head1 = new JsonArray();
    head1.add(new JsonString("This"));
    head1.add(new JsonString("a"));
    head1.add(new JsonString("is"));
    head1.add(new JsonString("header"));
    html1.add("head", head1);
    html1.add("tail", new JsonObject());
    obj1.add("html", html1);
    JsonObject obj2 = new JsonObject();
    JsonObject html2 = new JsonObject();
    JsonArray head2 = new JsonArray();
    head2.add(new JsonString("This"));
    head2.add(new JsonString("is"));
    head2.add(new JsonString("a"));
    head2.add(new JsonString("header"));
    html2.add("tail", new JsonObject());
    html2.add("head", head2);
    obj2.add("html", html2);
    assertNotEquals(obj1, obj2);
  }
}
