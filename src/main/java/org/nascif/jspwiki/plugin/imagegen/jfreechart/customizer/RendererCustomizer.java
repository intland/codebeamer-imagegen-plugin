package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.chart.renderer.AbstractRenderer;
import org.nascif.jspwiki.plugin.config.Customizer;

public class RendererCustomizer extends Customizer {

  public RendererCustomizer() {
    super(AbstractRenderer.class);
    registerParameterHandler("setBaseFillPaint", Paint.class);
    registerParameterHandler("setBaseItemLabelFont", Font.class);
    registerParameterHandler("setBaseItemLabelPaint", Paint.class);
    registerParameterHandler("setBaseItemLabelsVisible", boolean.class);
    registerParameterHandler("setBaseOutlinePaint", Paint.class);
    registerParameterHandler("setBaseOutlineStroke", Stroke.class);
    registerParameterHandler("setOutlineStroke", Stroke.class);
    registerParameterHandler("setBasePaint", Paint.class);
    registerParameterHandler("setBaseSeriesVisible", boolean.class);
    registerParameterHandler("setBaseSeriesVisibleInLegend", boolean.class);
    registerParameterHandler("setBaseStroke", Stroke.class);
    registerParameterHandler("setFillPaint", Paint.class);
    registerParameterHandler("setItemLabelFont", Font.class);
    registerParameterHandler("setItemLabelPaint", Paint.class);
    registerParameterHandler("setItemLabelsVisible", boolean.class);
    registerParameterHandler("setOutlinePaint", Paint.class);
    registerParameterHandler("setOutlineStroke", Stroke.class);
    registerParameterHandler("setPaint", Paint.class);
    registerParameterHandler("setSeriesVisible", Boolean.class);
    registerParameterHandler("setStroke", Stroke.class);
    
    registerParameterListHandler("setSeriesFillPaint", Paint.class);
    registerParameterListHandler("setSeriesOutlineStroke", Stroke.class);
    registerParameterListHandler("setSeriesPaint", Paint.class);
    registerParameterListHandler("setSeriesVisible", Boolean.class);
    registerParameterListHandler("setSeriesVisibleInLegend", Boolean.class);
    registerParameterListHandler("setSeriesOutlinePaint", Paint.class);
  }
}
