package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.util.Rotation;
import org.jfree.util.TableOrder;
import org.nascif.jspwiki.plugin.config.Customizer;

public class SpiderWebPlotCustomizer extends Customizer {
  
  public SpiderWebPlotCustomizer() {
    super(SpiderWebPlot.class);
    
    registerParameterHandler("setWebFilled", boolean.class) ;
    registerParameterHandler("setDataExtractOrder", TableOrder.class) ;
    registerParameterHandler("setHeadPercent", double.class) ;
    registerParameterHandler("setStartAngle", double.class) ;
    registerParameterHandler("setMaxValue", double.class) ;
    registerParameterHandler("setDirection", Rotation.class) ;
    registerParameterHandler("setInteriorGap", double.class) ;
    registerParameterHandler("setAxisLabelGap", double.class) ;
    registerParameterHandler("setSeriesPaint", Paint.class) ;
    registerParameterHandler("setBaseSeriesPaint", Paint.class) ;
    registerParameterHandler("setSeriesOutlinePaint", Paint.class) ;
    registerParameterHandler("setBaseSeriesOutlinePaint", Paint.class) ;
    registerParameterHandler("setSeriesOutlineStroke", Stroke.class) ;
    registerParameterHandler("setBaseSeriesOutlineStroke", Stroke.class) ;
    registerParameterHandler("setLabelFont", Font.class) ;
    registerParameterHandler("setLabelPaint", Paint.class) ;

    registerParameterListHandler("setSeriesPaint", Paint.class) ;
    registerParameterListHandler("setSeriesOutlinePaint", Paint.class) ;
    registerParameterListHandler("setSeriesOutlineStroke", Stroke.class) ;
  }
}
