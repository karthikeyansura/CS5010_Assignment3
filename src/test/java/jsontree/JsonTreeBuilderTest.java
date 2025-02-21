package jsontree;

import org.junit.Test;
import parser.InvalidJsonException;
import parser.JsonParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for validating the functionality of the JsonTreeBuilder.
 */
public class JsonTreeBuilderTest {

  /**
   * Helper method to feed the input string character-by-character.
   * It uses the JsonTreeBuilder to parse a JSON string into a JsonNode.
   *
   * @param json The JSON string to parse.
   * @return The root node of the resulting JSON tree.
   * @throws InvalidJsonException If the input JSON string is invalid.
   */
  private JsonNode treeBuild(String json) throws InvalidJsonException {
    JsonParser<JsonNode> parser = new JsonTreeBuilder();
    for (char c : json.toCharArray()) {
      parser.input(c);
    }
    return parser.output();
  }

  /**
   * Test case to verify the correct parsing of a JSON object with a root string.
   * Input: {"root":"h"}.
   */
  @Test
  public void testRootOfString() throws InvalidJsonException {
    String json = "{\"root\":\"h\"}";
    JsonNode node = treeBuild(json);
    assertNotNull(node);
    assertTrue(node instanceof JsonObject);
    assertTrue(node.prettyPrint().contains("\"root\":\"h\""));
  }

  /**
   * Test case to verify the parsing of a JSON object that contains an array.
   * Input: {"arr":["kn","ab"]}.
   */
  @Test
  public void testArrayInsideArray() throws InvalidJsonException {
    String json = "{\"arr\":[\"kn\",\"ab\"]}";
    JsonNode node = treeBuild(json);
    assertNotNull(node);
    assertTrue(node instanceof JsonObject);
    String pretty = node.prettyPrint();
    assertTrue(pretty.contains("\"arr\""));
    assertTrue(pretty.contains("\"kn\""));
    assertTrue(pretty.contains("\"ab\""));
  }

  /**
   * Test case to verify the correct handling of colons and commas in a JSON object.
   * Input: {"a":"b","c":"d"}.
   */
  @Test
  public void testColonAndComma() throws InvalidJsonException {
    String json = "{\"a\":\"b\",\"c\":\"d\"}";
    JsonNode node = treeBuild(json);
    assertNotNull(node);
    assertTrue(node instanceof JsonObject);
    String pretty = node.prettyPrint();
    assertTrue(pretty.contains("\"a\":\"b\""));
    assertTrue(pretty.contains("\"c\":\"d\""));
  }

  /**
   * Test case to verify that whitespace is ignored during parsing.
   * Input: " {     \"key\"  :  \"value\"     }".
   */
  @Test
  public void testWhitespace() throws InvalidJsonException {
    String json = " {     \"key\"  :  \"value\"     }";
    JsonNode node = treeBuild(json);
    assertNotNull(node);
    assertTrue(node instanceof JsonObject);
    String pretty = node.prettyPrint();
    assertTrue(pretty.contains("\"key\":\"value\""));
  }

  /**
   * Test case to verify the correct parsing of nested JSON objects and arrays.
   * Input: {"outer":{"inner":["one","two"]}}.
   */
  @Test
  public void testNestedStructure() throws InvalidJsonException {
    String json = "{\"out\":{\"in\":[\"one\",\"two\"]}}";
    JsonNode node = treeBuild(json);
    assertNotNull(node);
    assertTrue(node instanceof JsonObject);
    String pretty = node.prettyPrint();
    assertTrue(pretty.contains("\"out\""));
    assertTrue(pretty.contains("\"in\""));
    assertTrue(pretty.contains("\"one\""));
    assertTrue(pretty.contains("\"two\""));
  }


  @Test(expected = InvalidJsonException.class)
  public void testUnexpectedCharacter() throws InvalidJsonException {
    String json = "@";
    treeBuild(json);
  }

  @Test(expected = InvalidJsonException.class)
  public void testMismatchedBrace() throws InvalidJsonException {
    String json = "}{" + "}";
    treeBuild(json);
  }

  @Test(expected = InvalidJsonException.class)
  public void testMismatchedBracket() throws InvalidJsonException {
    String json = "][" + "]";
    treeBuild(json);
  }

  @Test(expected = InvalidJsonException.class)
  public void testInvalidValue() throws InvalidJsonException {
    String json = "{\"name\":5}";
    treeBuild(json);
  }

  @Test
  public void testReturnBuild() throws InvalidJsonException {
    JsonTreeBuilder build = new JsonTreeBuilder();
    JsonParser<JsonNode> returned = build.input('{')
            .input('"')
            .input('x')
            .input('"')
            .input(':')
            .input('"')
            .input('y')
            .input('"')
            .input('}');
    assertEquals(build, returned);
  }
}
