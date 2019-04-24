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
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.TextAnchor;
import org.jfree.util.SortOrder;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.CategoryPlotCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.CategoryDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.urls.WikiCategoryURLGenerator;

import com.ecyrd.jspwiki.WikiContext;

public class BarChartPlugin extends JFreeChartPlugin {

  private CategoryAxis categoryAxis;
  private ValueAxis valueAxis;

  protected boolean isImapSupported() {
    return true;
  }

  protected ChartDataBuilder createBuilder() {
    return new CategoryDataBuilder();
  }

  protected AbstractRenderer createRenderer(WikiContext context) {
    BarRenderer renderer;
    WikiCategoryURLGenerator urlGenerator = new WikiCategoryURLGenerator(context);
    if (m_3d) {
      categoryAxis = new CategoryAxis3D(m_xLabel);
      valueAxis = new NumberAxis3D(m_yLabel);
      renderer = new BarRenderer3D();
    } else {
      categoryAxis = new CategoryAxis(m_xLabel);
      valueAxis = new NumberAxis(m_yLabel);
      renderer = new BarRenderer();
    }
    if (useToolTips()) {
      renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    }
    if (useImap()) {
      renderer.setBaseItemURLGenerator(urlGenerator);
    }
    return renderer;
  }

  protected void customizeRenderer(AbstractRenderer renderer) {
    super.customizeRenderer(renderer);
  }

  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {
    CategoryPlot plot = null;

    plot = new CategoryPlot((CategoryDataset)dataset, categoryAxis, valueAxis, (CategoryItemRenderer) renderer);
    plot.setForegroundAlpha(0.75f);
    return plot;
  }

  protected void customizePlot(Plot plot) {
    super.customizePlot(plot);
    CategoryPlot cplot = (CategoryPlot) plot;
    BarRenderer renderer = (BarRenderer) cplot.getRenderer();
    if (cplot.getOrientation() == PlotOrientation.HORIZONTAL) {
      // change rendering order to ensure that bar overlapping is the right way around
      cplot.setRowRenderingOrder(SortOrder.DESCENDING);
      cplot.setColumnRenderingOrder(SortOrder.DESCENDING);

      ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3,
          TextAnchor.CENTER_LEFT);
      renderer.setBasePositiveItemLabelPosition(position1);

      ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9,
          TextAnchor.CENTER_RIGHT);
      renderer.setBaseNegativeItemLabelPosition(position2);
    } else {
      ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
          TextAnchor.BOTTOM_CENTER);
      renderer.setBasePositiveItemLabelPosition(position1);

      ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6,
          TextAnchor.TOP_CENTER);
      renderer.setBaseNegativeItemLabelPosition(position2);
    }
  }

  @Override
  protected Customizer createPlotCustomizer() {
    return new CategoryPlotCustomizer();
  }

}
