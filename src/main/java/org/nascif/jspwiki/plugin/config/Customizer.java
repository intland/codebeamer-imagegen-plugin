package org.nascif.jspwiki.plugin.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Customizer {

  protected final List<Setter> m_setterList;
  protected final Class m_class;

  protected Customizer (Class clazz) {
    m_class = clazz;
    m_setterList = new ArrayList<Setter>();
  }
      
  protected void registerParameterHandler(String tag, String methodName, Class paramClass) { 
    register(tag, methodName, new Class[] { paramClass }, false, null);
  }

  protected void registerParameterHandler(String methodName, Class paramClass) { 
    register(null, methodName, new Class[] { paramClass }, false, null);
  }

  protected void registerParameterHandler(String tag, String methodName, Class paramClass, 
      boolean required, String defaultValue) {
    register(tag, methodName, new Class[] { paramClass }, required, defaultValue);
  }

  protected void registerParameterHandler(String methodName, Class paramClass, 
      boolean required, String defaultValue) {
    register(null, methodName, new Class[] { paramClass }, required, defaultValue);
  }

  protected void registerParameterListHandler(String tag, String methodName, Class paramClass, 
      boolean required, String defaultValue) {
    register(tag, methodName, new Class[] { int.class, paramClass }, required, defaultValue);
  }

  protected void registerParameterListHandler(String methodName, Class paramClass, 
      boolean required, String defaultValue) {
    register(null, methodName, new Class[] { int.class, paramClass }, required, defaultValue);
  }

  protected void registerParameterListHandler(String methodName, Class paramClass) { 
    register(null, methodName, new Class[] { int.class, paramClass }, false, null);
  }

  protected void registerParameterListHandler(String tag, String methodName, Class paramClass) { 
    register(tag, methodName, new Class[] { int.class, paramClass }, false, null);
  }
  
  private final void register(String tag, String methodName, Class[] params, 
      boolean required, String defaultValue) {
    try {
      String paramName = tag == null? createParamFromMethod(methodName) : tag;
      Method setterMethod = m_class.getMethod(methodName, params);
      Setter Setter = 
        new Setter(paramName, setterMethod, required, defaultValue);  
      m_setterList.add(Setter);
    } catch (SecurityException e) {
      e.printStackTrace();
      throw new AssertionError("SecurityException not expected"); 
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      throw new AssertionError("NoSuchMethodException not expected"); 
    }
  }

  private String createParamFromMethod(String methodName) {
    if (!methodName.startsWith("set")) { 
      throw new AssertionError("Not following setter method naming convention: " + methodName);
    }
    return methodName.substring(3).toLowerCase();    
  }

  public void load(Map params) {
    for (Setter setter : m_setterList) {
      setter.read(params);
    }
  }

  public void apply(Object target) {
    if (!m_class.isInstance(target)) { 
      throw new AssertionError("target " + target + " is not an instance from the expected class " + m_class);
    }
    for (Setter setter : m_setterList) {
      setter.apply(target);
    }
  }
}
