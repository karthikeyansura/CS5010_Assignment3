package validator;

import org.junit.Before;
import org.junit.Test;
import parser.InvalidJsonException;

import static org.junit.Assert.assertEquals;

/**
 * Test class for JsonValidator.
 */
public class JsonValidatorTest {
  private JsonValidator validator;

  /**
   * Sets up the JsonValidator before each test.
   */
  @Before
  public void setUp() {
    validator = new JsonValidator();
  }

  /**
   * Test the case of an empty JSON.
   */
  @Test
  public void testEmpty() {
    assertEquals("Status:Empty", validator.output());
  }

  /**
   * Test the case of a simple key-value pair.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testSingleKV() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('"')
            .input('1')
            .input('2')
            .input('3')
            .input('"')
            .input('}');
    assertEquals("Status:Valid", validator.output());
  }

  /**
   * Test the case of multiple key value pairs.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testMultipleKV() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('"')
            .input('1')
            .input('2')
            .input('3')
            .input('"')
            .input(',')
            .input('"')
            .input('b')
            .input('"')
            .input(':')
            .input('"')
            .input('4')
            .input('5')
            .input('6')
            .input('"')
            .input('}');
    assertEquals("Status:Valid", validator.output());
  }

  /**
   * Test the case of missing colon between key and value.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test(expected = InvalidJsonException.class)
  public void testMissCol() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input('"')
            .input('1')
            .input('2')
            .input('3')
            .input('}');
  }

  /**
   * Test the case of an object inside another object.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testObjInObj() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('{')
            .input('"')
            .input('b')
            .input('"')
            .input(':')
            .input('"')
            .input('1')
            .input('"')
            .input('}')
            .input('}');
    assertEquals("Status:Valid", validator.output());
  }

  /**
   * Test the case of an array of strings.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testArrOfStr() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('[')
            .input('"')
            .input('1')
            .input('"')
            .input(',')
            .input('"')
            .input('2')
            .input('"')
            .input(',')
            .input('"')
            .input('3')
            .input('"')
            .input(']')
            .input('}');
    assertEquals("Status:Valid", validator.output());
  }

  /**
   * Test the case of an array of objects.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testArrOfObj() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('[')
            .input('{')
            .input('"')
            .input('b')
            .input('"')
            .input(':')
            .input('"')
            .input('1')
            .input('"')
            .input('}')
            .input(',')
            .input('{')
            .input('"')
            .input('c')
            .input('"')
            .input(':')
            .input('"')
            .input('2')
            .input('"')
            .input('}')
            .input(']')
            .input('}');
    assertEquals("Status:Valid", validator.output());
  }

  /**
   * Test the case of key starting with a number.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test(expected = InvalidJsonException.class)
  public void testKeyStartWithNum() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('1')
            .input('"')
            .input(':')
            .input('"')
            .input('a')
            .input('"')
            .input('}');
  }

  /**
   * Test the case of white spaces.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testWhSpc1() throws InvalidJsonException {
    validator.input('{')
            .input(' ')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input(' ')
            .input('"')
            .input('1')
            .input('"')
            .input('}');
    assertEquals("Status:Valid", validator.output());
  }

  /**
   * Test the case of incomplete.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testIncomplete() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('"')
            .input('1')
            .input('"');
    assertEquals("Status:Incomplete", validator.output());
  }

  /**
   * Test the case of missing comma.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test(expected = InvalidJsonException.class)
  public void testMissComma() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('"')
            .input('1')
            .input('2')
            .input('3')
            .input('"')
            .input('"')
            .input('b')
            .input('"')
            .input(':')
            .input('"')
            .input('4')
            .input('5')
            .input('6')
            .input('"')
            .input('}');
  }

  /**
   * Test the case of bracket mismatch.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test(expected = InvalidJsonException.class)
  public void testBrkFrmt() throws InvalidJsonException {
    validator.input('}')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('"')
            .input('1')
            .input('"')
            .input('{');
  }

  /**
   * Test the case of no key and no value.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testNokeyVal() throws InvalidJsonException {
    validator.input('{')
            .input('}');
    assertEquals("Status:Valid", validator.output());
  }

  /**
   * Test the case of only value.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test(expected = InvalidJsonException.class)
  public void testOnlyVal() throws InvalidJsonException {
    validator.input('{')
            .input(':')
            .input('"')
            .input('1')
            .input('"')
            .input('}');
  }

  /**
   * Test the case of only value.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test(expected = InvalidJsonException.class)
  public void testNoBrk() throws InvalidJsonException {
    validator.input('[')
            .input(':')
            .input('"')
            .input('1')
            .input('"')
            .input(']');
  }

  /**
   * Test the case of comma after comma.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test(expected = InvalidJsonException.class)
  public void testCommaErr() throws InvalidJsonException {
    JsonValidator validator = new JsonValidator();
    validator.input('{')
            .input('"')
            .input('k')
            .input('"')
            .input(':')
            .input('"')
            .input('v')
            .input('"')
            .input(',')
            .input(',')
            .input('}');
  }

  /**
   * Test the case of white spaces.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test
  public void testWhSpc2() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('"')
            .input(':')
            .input('"')
            .input(' ')
            .input('1')
            .input('"')
            .input('}');
    assertEquals("Status:Valid", validator.output());
  }

  /**
   * Test the case of special character in key.
   *
   * @throws InvalidJsonException if an error occurs during the check
   */
  @Test(expected = InvalidJsonException.class)
  public void testSpecChar() throws InvalidJsonException {
    validator.input('{')
            .input('"')
            .input('a')
            .input('$')
            .input('"')
            .input(':')
            .input('"')
            .input('1')
            .input('2')
            .input('3')
            .input('"')
            .input('}');
  }
}
