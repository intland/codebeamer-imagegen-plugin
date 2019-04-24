package org.nascif.jspwiki.plugin.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;

public class Setter {

  protected String m_tag;  
  protected Method m_method;
  protected Object[] m_args;
  protected boolean m_required;
  protected String m_defaultValue;
  protected boolean m_set;

  protected static final Logger log = Logger.getLogger(Setter.class);
  
  public Setter(String tag, Method method, boolean required, String defaultValue) {
    m_tag = tag; 
    m_method = method;
    m_required = required;
    m_defaultValue = defaultValue;
    m_set = false;
    validate();
  }      

  private void validate() {
    if (m_method == null) {
      throw new IllegalArgumentException("Method not set");
    }
    if (!isListSetter() && !isSingleSetter()) {
      throw new IllegalArgumentException("Invalid method signature: " + m_method);
    }
  }

  public void read(Map params) {
    String text = (String)params.remove(m_tag);
    if (null == text) {
      if (m_required) {
        throw new IllegalArgumentException(m_tag + " must be specified.");
      } else {
        if (null != m_defaultValue) {
          log.info("Param not defined, using default: " + m_tag+ " = " + m_defaultValue);
          setValue(m_defaultValue);
        }
      }
    } else {
      setValue(text);
    }
  }
  
  public void setValue(String value) {
    if (m_set) {
      log.warn("Param redefined! " + m_tag + " = " + value);
    } else {
      m_set = true;
      log.info("Param defined: " + m_tag + " = " + value);
    }

    Class[] params = m_method.getParameterTypes();
    if (isListSetter()) {
      String[] values = value.split(",");
      m_args = new Object[values.length];
      for (int i = 0; i < m_args.length; i++) {
        m_args[i] = transform(values[i], params[1]);
      }
    } else {
      m_args = new Object[1];
      m_args[0] = transform(value, params[0]);
    }
  }

  public void apply(Object target) {
    if (m_set) {
      if (isListSetter()) {
        Object[] listArg = new Object[2];
        for (int i = 0; i < m_args.length; i++) {
          listArg[0] = new Integer(i); 
          listArg[1] = m_args[i];  
          invoke(m_method, target, listArg);
        }
      } else {
        invoke(m_method, target, m_args);
      }
    }
  }


  private Object transform(String string, Class argClass) {
    Method transformMethod = Transformer.getTransform(argClass);
    if (transformMethod == null) {
      throw new IllegalArgumentException("Transformation not found for " + argClass);
    }
    return invoke(transformMethod, null, new Object[] {string} );
  }

  private boolean isListSetter() {
    Class[] args = m_method.getParameterTypes();
    return args.length == 2 && args[0].equals(int.class);
  }

  private boolean isSingleSetter() {
    Class[] args = m_method.getParameterTypes();
    return args.length == 1 && Transformer.getTransform(args[0]) != null;
  }

  private Object invoke(Method method, Object target, Object[] args) {
    Object result = null;
    try {
      result = method.invoke(target, args);
    } catch (IllegalAccessException e) {
      log.warn(e);
      assert false: "Unexpected IllegalAccessException on call to " + m_method.getName();
    } catch (InvocationTargetException e) {
      log.warn(e.getCause());
      throw new IllegalArgumentException("Invocation exception on call to " + m_method.getName(), e);
    }    
    return result;
  }

}
