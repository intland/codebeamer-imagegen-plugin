/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 2.1 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.nascif.jspwiki.plugin.imagegen.jfreechart;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.util.SortOrder;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.CategoryPlotCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.CategoryDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.urls.WikiCategoryURLGenerator;

import com.ecyrd.jspwiki.WikiContext;

public class StackedBarChartPlugin extends JFreeChartPlugin {

  CategoryAxis categoryAxis;
  ValueAxis valueAxis;

 protected boolean isImapSupported() {
    return true;
  }

  protected ChartDataBuilder createBuilder() {
    return new CategoryDataBuilder();
  }

  protected AbstractRenderer createRenderer(WikiContext context) {
    CategoryItemRenderer renderer;
    if (m_3d) {
        categoryAxis = new CategoryAxis3D(m_xLabel);
        valueAxis = new NumberAxis3D(m_yLabel);
        renderer = new StackedBarRenderer3D();
    } else {
      categoryAxis = new CategoryAxis(m_xLabel);
      valueAxis = new NumberAxis(m_yLabel);
      renderer = new StackedBarRenderer();
    }
    if (useImap()) {
      renderer.setBaseItemURLGenerator(new WikiCategoryURLGenerator(context));
    }
    if (useToolTips()) {
      renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    }
    return (AbstractRenderer)renderer;
  }

  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {
    CategoryPlot plot = new CategoryPlot((CategoryDataset)dataset, categoryAxis, valueAxis, (CategoryItemRenderer)renderer);
    return plot;
  }

  protected void customizePlot(Plot plot) {
    super.customizePlot(plot);
    CategoryPlot cplot = (CategoryPlot) plot;
    if (cplot.getOrientation() == PlotOrientation.HORIZONTAL) {
      // change rendering order to ensure that bar overlapping is the right way around
      cplot.setColumnRenderingOrder(SortOrder.DESCENDING);
    }
  }

  @Override
  protected Customizer createPlotCustomizer() {
    return new CategoryPlotCustomizer();
  }

}
