package org.nascif.jspwiki.plugin.config;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jfree.chart.ChartColor;

public class CustomizerTest extends TestCase {

  Customizer _customizer;
  Map<String,String> _params;
  Target _target;
  
  public CustomizerTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    _customizer = new Customizer(Target.class);
    
    _customizer.registerParameterHandler("setBoolean", Boolean.class);
    _customizer.registerParameterHandler("setFont",    Font.class);
    _customizer.registerParameterHandler("setPaint",   Paint.class); 
    _customizer.registerParameterHandler("setString",  String.class); 
    _customizer.registerParameterHandler("setStroke",  Stroke.class);
    _customizer.registerParameterHandler("setPBboolean", boolean.class);
    _customizer.registerParameterHandler("setPFloat",   float.class);
    _customizer.registerParameterHandler("setPDouble",  double.class);
    
    _params = new HashMap<String,String>();
    
    _target = new Target();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCustomizerBoolean() {
    _params.put("boolean", "True");
    _customizer.load(_params);
    _customizer.apply(_target);

    assertEquals(Boolean.TRUE, _target.getBoolean());
  }
  
  public void testCustomizerFont() {
    _params.put("font",    "Courier");
    _customizer.load(_params);
    _customizer.apply(_target);

    assertEquals(Font.decode("Courier"), _target.getFont());
  }
  
  public void testCustomizerPaint() {    
    _params.put("paint",   "DARK_CYAN"); 
    _customizer.load(_params);
    _customizer.apply(_target);
    
    assertEquals(ChartColor.DARK_CYAN, _target.getPaint());
  }

  public void testCustomizerPaintCamelCase() {    
    _params.put("paint",   "darkCyan"); 
    _customizer.load(_params);
    _customizer.apply(_target);
    
    assertEquals(ChartColor.DARK_CYAN, _target.getPaint());
  }
  
  public void testCustomizerPaintRGB() {    
    _params.put("paint",   "#00C0C0"); 
    _customizer.load(_params);
    _customizer.apply(_target);
    
    assertEquals(ChartColor.DARK_CYAN, _target.getPaint());
  }
  
  public void testCustomizerString() {
    _params.put("string",  "Hello World"); 
    _customizer.load(_params);
    _customizer.apply(_target);
    
    assertEquals("Hello World", _target.getString());
  }
  
  public void testCustomizerStroke() {
    _params.put("stroke",  "14");
    _customizer.load(_params);
    _customizer.apply(_target);
    
    assertEquals(new BasicStroke(14), _target.getStroke());
  }
  
  public void testCustomizerPBoolean() {
    _params.put("pboolean", "false");
    _customizer.load(_params);
    _customizer.apply(_target);

    assertEquals(false, _target.getPBoolean());
  }
  
  public void testCustomizerPFloat() {    
    _params.put("pfloat",   "12");
    _customizer.load(_params);
    _customizer.apply(_target);

    assertEquals(12.0, _target.getPFloat(), 0.1);
  }

  public void testCustomizerPDouble() {    
    _params.put("pdouble",   "3.1415");
    _customizer.load(_params);
    _customizer.apply(_target);

    assertEquals(3.1415, _target.getPDouble(), 0.1);
  }

}
