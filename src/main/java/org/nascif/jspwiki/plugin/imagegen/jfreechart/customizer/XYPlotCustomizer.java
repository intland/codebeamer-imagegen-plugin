package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;

public class XYPlotCustomizer extends PlotCustomizer {

  public XYPlotCustomizer() {
    this(XYPlot.class);
  }
  
  private XYPlotCustomizer(Class clazz) {
    super(clazz);
    registerParameterHandler("setOrientation", PlotOrientation.class, false, "HORIZONTAL");
    registerParameterHandler("setDatasetRenderingOrder", DatasetRenderingOrder.class);
    registerParameterHandler("setSeriesRenderingOrder", SeriesRenderingOrder.class);
    registerParameterHandler("setWeight", int.class);
    registerParameterHandler("setDomainGridlinesVisible", boolean.class);
    registerParameterHandler("setDomainGridlineStroke", Stroke.class);
    registerParameterHandler("setDomainGridlinePaint", Paint.class);
    registerParameterHandler("setRangeGridlinesVisible", boolean.class);
    registerParameterHandler("setRangeGridlineStroke", Stroke.class);
    registerParameterHandler("setRangeGridlinePaint", Paint.class);
    registerParameterHandler("setRangeZeroBaselineVisible", boolean.class);
    registerParameterHandler("setRangeZeroBaselineStroke", Stroke.class);
    registerParameterHandler("setRangeZeroBaselinePaint", Paint.class);
    registerParameterHandler("setDomainTickBandPaint", Paint.class);
    registerParameterHandler("setRangeTickBandPaint", Paint.class);
    registerParameterHandler("setDomainCrosshairVisible", boolean.class);
    registerParameterHandler("setDomainCrosshairLockedOnData", boolean.class);
    registerParameterHandler("setDomainCrosshairValue", double.class);
    registerParameterHandler("setDomainCrosshairStroke", Stroke.class);
    registerParameterHandler("setDomainCrosshairPaint", Paint.class);
    registerParameterHandler("setRangeCrosshairVisible", boolean.class);
    registerParameterHandler("setRangeCrosshairLockedOnData", boolean.class);
    registerParameterHandler("setRangeCrosshairValue", double.class);
    registerParameterHandler("setRangeCrosshairStroke", Stroke.class);
    registerParameterHandler("setRangeCrosshairPaint", Paint.class);
  }

}
