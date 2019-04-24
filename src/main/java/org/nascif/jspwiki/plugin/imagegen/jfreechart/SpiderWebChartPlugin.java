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

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.NullCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.SpiderWebPlotCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.CategoryDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;

import com.ecyrd.jspwiki.WikiContext;

public class SpiderWebChartPlugin extends JFreeChartPlugin {

 protected boolean isImapSupported() {
    return false;
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
    return new SpiderWebPlotCustomizer();
  }

  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {
    SpiderWebPlot plot = new SpiderWebPlot((CategoryDataset)dataset);
    return plot;
  }

}
