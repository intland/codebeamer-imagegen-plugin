package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.chart.plot.PolarPlot;
import org.nascif.jspwiki.plugin.config.Customizer;

public class PolarPlotCustomizer extends Customizer {

  public PolarPlotCustomizer() {
    this(PolarPlot.class);
  }

  private PolarPlotCustomizer(Class clazz) {
    super(clazz);
    registerParameterHandler("setAngleLabelsVisible", boolean.class);
    registerParameterHandler("setAngleLabelFont", Font.class);
    registerParameterHandler("setAngleLabelPaint", Paint.class);
    registerParameterHandler("setAngleGridlinesVisible", boolean.class);
    registerParameterHandler("setAngleGridlineStroke", Stroke.class);
    registerParameterHandler("setAngleGridlinePaint", Paint.class);
    registerParameterHandler("setRadiusGridlinesVisible", boolean.class);
    registerParameterHandler("setRadiusGridlineStroke", Stroke.class);
    registerParameterHandler("setRadiusGridlinePaint", Paint.class);
  }

}
