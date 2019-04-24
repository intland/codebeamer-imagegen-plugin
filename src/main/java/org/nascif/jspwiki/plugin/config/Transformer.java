package org.nascif.jspwiki.plugin.config;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.util.Rotation;
import org.jfree.util.SortOrder;
import org.jfree.util.TableOrder;

public class Transformer {
  
  private static Map<Class, Method> c_transformMap;
  protected String m_tag;  
  protected Method m_method;
  protected Object[] m_args;
  protected boolean m_required;
  protected String m_defaultValue;
  protected static final Logger log = Logger.getLogger(Transformer.class);
  private static Pattern RGB_PATTERN = 
    Pattern.compile("#([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})");

  static {
    registerTransformations();
  }
  
  public static Object transform_Boolean(String value) {
    return Boolean.valueOf(value);
  }
  
  public static Object transform_Font(String value) {
    return Font.decode(value);
  }
  
  public static Object transform_Paint(String value) {
    Paint result = null;
    try {
      Matcher m = RGB_PATTERN.matcher(value);
      if (m.matches()) {
        int red = Integer.parseInt(m.group(1), 16);
        int green = Integer.parseInt(m.group(2), 16);
        int blue = Integer.parseInt(m.group(3), 16);
        result = new Color(red, green, blue);
      } else {
        Field colorField = ImageGenColor.class.getField(value.toUpperCase());
        result = (Paint)colorField.get(null);
      }
    } catch (NoSuchFieldException e) {
      log.warn(e);
      throw new IllegalArgumentException("Invalid color: " + value);
    } catch (IllegalArgumentException e) {
      log.warn(e);
      throw new IllegalArgumentException("Invalid color: " + value);
    } catch (IllegalAccessException e) {
      log.warn(e);
      throw new IllegalArgumentException("Invalid color: " + value);
    } catch (ClassCastException e) {
      log.warn(e);
      throw new IllegalArgumentException("Invalid color: " + value);
    }
    return result;
  }
  
  public static Object transform_String(String value) {
    return value;
  }
  
  public static Object transform_Stroke(String value) {
    return new BasicStroke(Float.parseFloat(value));    
  }
  
  public static Object transform_boolean(String value) {
    return Boolean.valueOf(value);    
  }
  
  public static Object transform_float(String value) {
    return Float.valueOf(value);
  }

  public static Object transform_int(String value) {
    return Integer.valueOf(value);
  }

  public static Object transform_double(String value) {
    return Double.valueOf(value);
  }

  public static PlotOrientation transform_PlotOrientation(String value) {
    PlotOrientation result = null;
    String text = value.toUpperCase();
    if (text.equals("VERTICAL")) {
      result = PlotOrientation.VERTICAL;
    } else {
      if (text.equals("HORIZONTAL")) {
        result = PlotOrientation.HORIZONTAL;
      } else {
        throw new IllegalArgumentException("Invalid orientation type: " + value);
      }
    }
    return result;
  }
  
  public static TableOrder transform_TableOrder(String value) {
    TableOrder result = null;
    String text = value.toUpperCase();
    if (text.equals("BY_ROW")) {
      result = TableOrder.BY_ROW;
    } else {
      if (text.equals("BY_COLUMN")) {
        result = TableOrder.BY_COLUMN;
      } else {
        throw new IllegalArgumentException("Invalid table order: " + value);
      }
    }
    return result;
  }

  public static SortOrder transform_SortOrder(String value) {
    SortOrder result = null;
    String text = value.toUpperCase();
    if (text.equals("ASCENDING")) {
      result = SortOrder.ASCENDING;
    } else {
      if (text.equals("DESCENDING")) {
        result = SortOrder.DESCENDING;
      } else {
        throw new IllegalArgumentException("Invalid sort order: " + value);
      }
    }
    return result;
  }

  public static DatasetRenderingOrder transform_DatasetRenderingOrder(String value) {
    DatasetRenderingOrder result = null;
    String text = value.toUpperCase();
    if (text.equals("FORWARD")) {
      result = DatasetRenderingOrder.FORWARD;
    } else {
      if (text.equals("REVERSE")) {
        result = DatasetRenderingOrder.REVERSE;
      } else {
        throw new IllegalArgumentException("Invalid dataset rendering order: " + value);
      }
    }
    return result;
  }

  public static SeriesRenderingOrder transform_SeriesRenderingOrder(String value) {
    SeriesRenderingOrder result = null;
    String text = value.toUpperCase();
    if (text.equals("FORWARD")) {
      result = SeriesRenderingOrder.FORWARD;
    } else {
      if (text.equals("REVERSE")) {
        result = SeriesRenderingOrder.REVERSE;
      } else {
        throw new IllegalArgumentException("Invalid series rendering order: " + value);
      }
    }
    return result;
  }
  
  public static Rotation transform_Rotation(String value) {
    Rotation result = null;
    String text = value.toUpperCase();
    if (text.equals("CLOCKWISE")) {
      result = Rotation.CLOCKWISE;
    } else {
      if (text.equals("ANTICLOCKWISE")) {
        result = Rotation.ANTICLOCKWISE;
      } else {
        throw new IllegalArgumentException("Invalid rotation: " + value);
      }
    }
    return result;
  }
  
  public static Method getTransform(Class argClass) {
    return (Method)c_transformMap.get(argClass);
  }

  private static void registerTransformations() {
    c_transformMap = new HashMap<Class, Method>();
    registerTransformation(Boolean.class);
    registerTransformation(Font.class);
    registerTransformation(Paint.class);
    registerTransformation(String.class);
    registerTransformation(Stroke.class);
    registerTransformation(boolean.class);
    registerTransformation(float.class);
    registerTransformation(int.class);
    registerTransformation(double.class);
    registerTransformation(Rotation.class);
    registerTransformation(TableOrder.class);
    registerTransformation(SortOrder.class);
    registerTransformation(PlotOrientation.class );
    registerTransformation(DatasetRenderingOrder.class);
    registerTransformation(SeriesRenderingOrder.class);
  }
  
  private static void registerTransformation(Class key) {
    String name = key.getName(); 
    int idx = name.lastIndexOf('.');
    String uname = name;
    if (idx >= 0) {
      uname = name.substring(name.lastIndexOf('.') + 1);
    }
    String methodName = "transform_" + uname;
    registerTransformation(key, methodName);
  }

  private static void registerTransformation(Class key, String name) {
    Method method;
    try {
      method = Transformer.class.getMethod(name, new Class[] { String.class });
      if (method == null) {
        throw new AssertionError("No method named " + name);
      }
      c_transformMap.put(key, method);
    } catch (SecurityException e) {
      log.warn(e);
      throw new AssertionError("Unexpected SecurityException for " + name);
    } catch (NoSuchMethodException e) {
      log.warn(e);
      throw new AssertionError("Unexpected NoSuchMethodException for " + name);
    }
  }
}
