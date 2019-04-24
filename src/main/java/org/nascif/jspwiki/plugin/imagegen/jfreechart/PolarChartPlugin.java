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

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.PolarPlotCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.XYDataBuilder;

import com.ecyrd.jspwiki.WikiContext;

        
public class PolarChartPlugin extends JFreeChartPlugin
{
  public static final String PARAM_DATEFORMAT = "dateformat";

  protected boolean isImapSupported() {
    return false;
  }

  protected ChartDataBuilder createBuilder() {
    return new XYDataBuilder();
  }

  protected AbstractRenderer createRenderer(WikiContext context) {
    return new DefaultPolarItemRenderer();
  }

  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {
    PolarPlot plot = new PolarPlot();
    plot.setDataset((XYDataset)dataset);
    NumberAxis rangeAxis = new NumberAxis();
    rangeAxis.setAxisLineVisible(false);
    rangeAxis.setTickMarksVisible(false);
    rangeAxis.setTickLabelInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
    plot.setAxis(rangeAxis);
    plot.setRenderer((PolarItemRenderer) renderer);
    return plot;
  }

  @Override
  protected Customizer createPlotCustomizer() {
    return new PolarPlotCustomizer();
  }

}
