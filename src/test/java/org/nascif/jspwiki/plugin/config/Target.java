package org.nascif.jspwiki.plugin.config;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

public class Target {

  private Boolean _boolean;
  private Font    _font;
  private Paint   _paint;  
  private String  _string; 
  private Stroke  _stroke; 
  private boolean _pboolean;
  private float   _float;
  private double  _double;
  private ArrayList<String> _list;      

  public Target() {
    super();
    _list = new ArrayList<String>();
  }

  public Boolean getBoolean() {
    return _boolean;
  }

  public void setBoolean(Boolean v) {
    _boolean = v;
  }

  public double getPDouble() {
    return _double;
  }

  public void setPDouble(double v) {
    _double = v;
  }

  
  public Font getFont() {
    return _font;   
  } 

  public void setFont(Font v) {
    _font = v;   
  }

  public Paint getPaint() {
    return _paint;  
  }
  
  public void setPaint(Paint v) {
    _paint = v;
  }
  
  public String getString() {
    return _string; 
  }
  
  public void setString(String v) {
    _string = v;
  }
  
  public Stroke getStroke() {
    return _stroke; 
  }
  
  public void setStroke(Stroke v) {
    _stroke = v;
  }
  
  public boolean getPBoolean() {
    return _pboolean;
  }
  
  public void setPBboolean(boolean v) {
    _pboolean = v;
  }
  
  public float getPFloat() {
    return _float;      
  }
  
  public void setPFloat(float v) {
    _float = v;
  }

  public void setStringList(int index, String value) {
    while (_list.size() <= index) {
      _list.add(null);
    }
    _list.set(index, value);
  }

  public List getStringList() {
    return _list;
  }
  
  
  
}
