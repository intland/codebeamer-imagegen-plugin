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

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.TableOrder;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.NullCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.CategoryDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.urls.WikiPieURLGenerator;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.MultiplePiePlotCustomizer;

import com.ecyrd.jspwiki.WikiContext;

public class MultiplePieChartPlugin extends JFreeChartPlugin {

  protected boolean isImapSupported() {
    return true;
  }

  protected ChartDataBuilder createBuilder() {
    return new CategoryDataBuilder();
  }

  protected AbstractRenderer createRenderer(WikiContext context) {
    return null;
  }

    protected Customizer createRendererCustomizer() {
    return new NullCustomizer(); 
  }

  protected Customizer createPlotCustomizer() {
    return new MultiplePiePlotCustomizer();
  }
  
  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {

    TableOrder order = TableOrder.BY_COLUMN;
    MultiplePiePlot plot = new MultiplePiePlot((CategoryDataset)dataset);
    plot.setDataExtractOrder(order);
    plot.setBackgroundPaint(null);
    plot.setOutlineStroke(null);
    if (m_3d) {
      JFreeChart pieChart = new JFreeChart(new PiePlot3D(null));
      TextTitle seriesTitle = new TextTitle("Series Title", new Font("SansSerif", Font.BOLD, 12));
      seriesTitle.setPosition(RectangleEdge.BOTTOM);
      pieChart.setTitle(seriesTitle);
      pieChart.setBackgroundPaint(null);
      plot.setPieChart(pieChart);
    }
    if (useImap() || useToolTips()) {
      PiePlot pp = (PiePlot) plot.getPieChart().getPlot();
      if (useImap()) {
        PieURLGenerator urlGenerator = new WikiPieURLGenerator(context);
        pp.setURLGenerator(urlGenerator);
      }
      if (useToolTips()) {
        PieToolTipGenerator tooltipGenerator = new StandardPieToolTipGenerator();
        pp.setToolTipGenerator(tooltipGenerator);
      }      
    }
    return plot;
  }
}
