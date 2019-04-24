package org.nascif.jspwiki.plugin.config;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import junit.framework.TestCase;

public class TransformerTest extends TestCase {

  public TransformerTest() {
    super();
  }

  public TransformerTest(String arg0) {
    super(arg0);
  }

  public void testTransformationBoolean() {
    assertEquals(Boolean.TRUE, Transformer.transform_Boolean("true"));
    assertEquals(Boolean.FALSE, Transformer.transform_Boolean("False"));
    
    // we just follow Java behavior, could be more stringent
    assertEquals(Boolean.FALSE, Transformer.transform_Boolean("blah"));
  }

  public void testTransformationFont() {
    String value = "TimesNewRoman-italic-12";
    assertEquals(Font.decode(value), Transformer.transform_Font(value));

    value = "Courier bold";
    assertEquals(Font.decode(value), Transformer.transform_Font(value));
    
    // won't fail, but you will get Java's best match to the described font.
    Transformer.transform_Font("xsdsdw123223");
  }

  public void testTransformationPaint() {
    assertEquals(Color.BLUE, Transformer.transform_Paint("blue"));
    assertEquals(Color.RED, Transformer.transform_Paint("#FF0000"));
    try {
      Transformer.transform_Paint("blah");
      fail("Should have failed");
    } catch (IllegalArgumentException e) {
      //expected
      e.printStackTrace();
    }
  }

  public void testTransformationString() {
    assertEquals("Hello", Transformer.transform_String("Hello"));
  }

  public void testTransformationStroke() {
    assertEquals(new BasicStroke(12), Transformer.transform_Stroke("12"));
    try {
      Transformer.transform_Stroke("blah");
      fail("Should have failed");
    } catch (IllegalArgumentException e) {
      //expected
      e.printStackTrace();
    }
  }

  public void testTransformationboolean() {
    assertEquals(true, Transformer.transform_boolean("true"));
    assertEquals(false, Transformer.transform_boolean("false"));

    // following Java convention
    assertEquals(false, Transformer.transform_boolean("blah"));
  }

  public void testTransformationfloat() {
    assertEquals(1.234, ((Float)Transformer.transform_float("1.234")).floatValue(), 0.10);
    assertEquals(100.00, ((Float)Transformer.transform_float("100")).floatValue(), 0.10);
    assertEquals(-5.678, ((Float)Transformer.transform_float("-5.678")).floatValue(), 0.10);
    try {
      Transformer.transform_float("blah");
      fail("Should have failed");
    } catch (IllegalArgumentException e) {
      //expected
      e.printStackTrace();
    }
  }

}
