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

import java.text.DateFormat;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.general.Dataset;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.CategoryPlotCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.GanttDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.urls.WikiCategoryURLGenerator;

import com.ecyrd.jspwiki.WikiContext;

public class GanttChartPlugin extends JFreeChartPlugin {

  private CategoryAxis categoryAxis;
  private DateAxis dateAxis;

  protected boolean isImapSupported() {
    return true;
  }

  protected ChartDataBuilder createBuilder() {
    return new GanttDataBuilder();
  }

  protected AbstractRenderer createRenderer(WikiContext context) {
    GanttRenderer renderer = new GanttRenderer();
    if (useToolTips()) {
      renderer.setBaseToolTipGenerator(
          new IntervalCategoryToolTipGenerator("{3} - {4}", DateFormat.getDateInstance())
      );
    }
    if (useImap()) {
      renderer.setBaseItemURLGenerator(new WikiCategoryURLGenerator(context));
    }
    return renderer;
  }

  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {
    categoryAxis = new CategoryAxis(m_yLabel);
    dateAxis = new DateAxis(m_xLabel);
    CategoryPlot plot = new CategoryPlot((IntervalCategoryDataset)dataset,
        categoryAxis, dateAxis, (CategoryItemRenderer)renderer);
    plot.setNoDataMessage("No data available");
    return plot;
  }

  @Override
  protected Customizer createPlotCustomizer() {
    return new CategoryPlotCustomizer();
  }

  @Override
  protected void customizePlot(Plot plot) {
    super.customizePlot(plot);
    ((CategoryPlot) plot).setOrientation(PlotOrientation.HORIZONTAL);
  }

}
