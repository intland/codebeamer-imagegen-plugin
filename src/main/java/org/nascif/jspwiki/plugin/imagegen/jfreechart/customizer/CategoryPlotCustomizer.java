package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.util.SortOrder;

public class CategoryPlotCustomizer extends PlotCustomizer {

  private CategoryPlotCustomizer(Class clazz) {
    super(clazz);
    registerParameterHandler("setOrientation", PlotOrientation.class, false, "VERTICAL");
    registerParameterHandler("setColumnRenderingOrder", SortOrder.class);
    registerParameterHandler("setRowRenderingOrder", SortOrder.class);
    registerParameterHandler("setDomainGridlinesVisible", boolean.class);
    registerParameterHandler("setDomainGridlineStroke", Stroke.class);
    registerParameterHandler("setDomainGridlinePaint", Paint.class);
    registerParameterHandler("setRangeGridlinesVisible", boolean.class);
    registerParameterHandler("setRangeGridlineStroke", Stroke.class);
    registerParameterHandler("setRangeGridlinePaint", Paint.class);
    registerParameterHandler("setRangeCrosshairVisible", boolean.class);
    registerParameterHandler("setRangeCrosshairLockedOnData", boolean.class);
    registerParameterHandler("setRangeCrosshairValue", double.class);
    registerParameterHandler("setRangeCrosshairStroke", Stroke.class);
    registerParameterHandler("setRangeCrosshairPaint", Paint.class);
    registerParameterHandler("setWeight", int.class);
    registerParameterHandler("setAnchorValue", double.class);
  }

  public CategoryPlotCustomizer() {
    this(CategoryPlot.class);
  }

}
