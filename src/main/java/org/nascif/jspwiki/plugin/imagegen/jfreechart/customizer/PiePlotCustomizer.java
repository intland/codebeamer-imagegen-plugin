package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.chart.plot.PiePlot;
import org.nascif.jspwiki.plugin.config.Customizer;

public class PiePlotCustomizer extends Customizer {
  
  public PiePlotCustomizer() {
    this(PiePlot.class);
  }

  private PiePlotCustomizer(Class clazz) {
    super(clazz);
    registerParameterHandler("setStartAngle", double.class);
    registerParameterHandler("setInteriorGap", double.class);
    registerParameterHandler("setCircular", boolean.class);
    registerParameterHandler("setIgnoreNullValues", boolean.class);
    registerParameterHandler("setIgnoreZeroValues", boolean.class);
    registerParameterHandler("setBaseSectionPaint", Paint.class);
    registerParameterHandler("setSectionOutlinesVisible", boolean.class);
    registerParameterHandler("setBaseSectionOutlinePaint", Paint.class);
    registerParameterHandler("setSectionOutlineStroke", Stroke.class);
    registerParameterHandler("setBaseSectionOutlineStroke", Stroke.class);
    registerParameterHandler("setShadowPaint", Paint.class);
    registerParameterHandler("setShadowXOffset", double.class);
    registerParameterHandler("setShadowYOffset", double.class);
    registerParameterHandler("setLabelGap", double.class);
    registerParameterHandler("setMaximumLabelWidth", double.class);
    registerParameterHandler("setLabelLinksVisible", boolean.class);
    registerParameterHandler("setLabelLinkMargin", double.class);
    registerParameterHandler("setLabelLinkPaint", Paint.class);
    registerParameterHandler("setLabelLinkStroke", Stroke.class);
    registerParameterHandler("setLabelFont", Font.class);
    registerParameterHandler("setLabelPaint", Paint.class);
    registerParameterHandler("setLabelBackgroundPaint", Paint.class);
    registerParameterHandler("setLabelOutlinePaint", Paint.class);
    registerParameterHandler("setLabelOutlineStroke", Stroke.class);
    registerParameterHandler("setLabelShadowPaint", Paint.class);
    registerParameterHandler("setMinimumArcAngleToDraw", double.class);

    registerParameterListHandler("setSectionOutlinePaint", Paint.class);
    registerParameterListHandler("setSectionPaint", Paint.class);
    registerParameterListHandler("setExplodePercent", double.class);
  }
}
