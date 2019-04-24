package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import java.awt.Paint;

import org.nascif.jspwiki.plugin.config.Customizer;

public class PlotCustomizer extends Customizer {
  
  protected PlotCustomizer(Class clazz) {
    super(clazz);
    registerParameterHandler("setBackgroundPaint", Paint.class);
    registerParameterHandler("setBackgroundAlpha", float.class);
    registerParameterHandler("setNoDataMessage", String.class);
    registerParameterHandler("setOutlinePaint", Paint.class);    
  }
}
