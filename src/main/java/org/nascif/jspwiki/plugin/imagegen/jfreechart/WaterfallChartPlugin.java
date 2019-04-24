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

import java.awt.Color;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.WaterfallBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.Layer;
import org.jfree.ui.TextAnchor;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.CategoryPlotCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.CategoryDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.urls.WikiCategoryURLGenerator;

import com.ecyrd.jspwiki.WikiContext;

public class WaterfallChartPlugin extends JFreeChartPlugin {

  private CategoryAxis categoryAxis;
  private ValueAxis valueAxis;

  protected boolean isImapSupported() {
    return true;
  }

  protected ChartDataBuilder createBuilder() {
    return new CategoryDataBuilder();
  }

  protected AbstractRenderer createRenderer(WikiContext context) {
    categoryAxis = new CategoryAxis(m_xLabel);
    categoryAxis.setCategoryMargin(0.0);

    valueAxis = new NumberAxis(m_yLabel);

    WaterfallBarRenderer renderer = new WaterfallBarRenderer();
    if (useToolTips()) {
      renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    }
    if (useImap()) {
      renderer.setBaseItemURLGenerator(new WikiCategoryURLGenerator(context));
    }
    return renderer;
  }

  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {
    CategoryPlot plot =
      new CategoryPlot((CategoryDataset)dataset, categoryAxis, valueAxis,
            (WaterfallBarRenderer)renderer);
    plot.clearRangeMarkers();
    Marker baseline = new ValueMarker(0.0);
    baseline.setPaint(Color.black);
    plot.addRangeMarker(baseline, Layer.FOREGROUND);
    return plot;
  }

  @Override
  protected Customizer createPlotCustomizer() {
    return new CategoryPlotCustomizer();
  }

  protected void customizePlot(Plot plot) {
    super.customizePlot(plot);
    WaterfallBarRenderer renderer = (WaterfallBarRenderer)(((CategoryPlot)plot).getRenderer());
    if (((CategoryPlot)plot).getOrientation() == PlotOrientation.HORIZONTAL) {
      ItemLabelPosition position = new ItemLabelPosition(
          ItemLabelAnchor.CENTER, TextAnchor.CENTER,
          TextAnchor.CENTER, Math.PI / 2.0
      );
      renderer.setBasePositiveItemLabelPosition(position);
      renderer.setBaseNegativeItemLabelPosition(position);
    } else {
      ItemLabelPosition position = new ItemLabelPosition(
          ItemLabelAnchor.CENTER, TextAnchor.CENTER,
          TextAnchor.CENTER, 0.0
      );
      renderer.setBasePositiveItemLabelPosition(position);
      renderer.setBaseNegativeItemLabelPosition(position);
    }
  }
}
