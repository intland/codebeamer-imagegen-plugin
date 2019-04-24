package org.nascif.jspwiki.plugin.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class SetterTest extends TestCase {

  Target _target;
  Map<String, String> _params;
  
  public SetterTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    _target = new Target();
    _params = new HashMap<String, String>();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testRequiredPresent() throws SecurityException, NoSuchMethodException {
    Method setBoolean = Target.class.getMethod("setBoolean", Boolean.class);
    Setter setter =  
      new Setter("boolean", setBoolean, true, null);
    _params.put("boolean", "true");
    setter.read(_params);
    setter.apply(_target);
    assertEquals(Boolean.TRUE, _target.getBoolean());
  }

  public void testRequiredMissing() throws SecurityException, NoSuchMethodException {
    Method setBoolean = Target.class.getMethod("setBoolean", Boolean.class);
    try {
      Setter setter =  
        new Setter("boolean", setBoolean, true, null);
      setter.read(_params);
      fail("should have failed");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  public void testUseDefaultValue() throws SecurityException, NoSuchMethodException {
    Method setString = Target.class.getMethod("setString", String.class);
    Setter setter =  
      new Setter("string", setString, false, "Hello");
    setter.read(_params);
    setter.apply(_target);
    assertEquals("Hello", _target.getString());
  }

  public void testListAssignment() throws SecurityException, NoSuchMethodException {
    Method setStringList = Target.class.getMethod("setStringList", int.class, String.class);
    Setter setter =  
      new Setter("stringList", setStringList, false, null);
    _params.put("stringList", "a,b,c");
    setter.read(_params);
    setter.apply(_target);
    List result = _target.getStringList();
    assertEquals(3, result.size());
    assertEquals("a", result.get(0));
    assertEquals("b", result.get(1));
    assertEquals("c", result.get(2));
  }
  
}
