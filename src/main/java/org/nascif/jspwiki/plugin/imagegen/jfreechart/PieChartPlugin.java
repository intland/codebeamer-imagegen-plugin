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

import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.NullCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.PiePlotCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.PieDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.urls.WikiPieURLGenerator;

import com.ecyrd.jspwiki.WikiContext;

public class PieChartPlugin extends JFreeChartPlugin {

  protected boolean isImapSupported() {
    return true;
  }

  protected ChartDataBuilder createBuilder() {
    return new PieDataBuilder();
  }

  protected AbstractRenderer createRenderer(WikiContext context) {
    return null;
  }

  protected Customizer createRendererCustomizer() {
    return new NullCustomizer(); 
  }

  protected Customizer createPlotCustomizer() {
    return new PiePlotCustomizer();
  }
  
  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {
    PiePlot plot;
    if (m_3d) { 
      plot = new PiePlot3D((PieDataset)dataset);
    } else {
      plot = new PiePlot((PieDataset)dataset);
    }
    if (useToolTips()) {
      if (m_3d) { 
        plot.setToolTipGenerator(new StandardPieToolTipGenerator());
      } else {
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator());
        plot.setToolTipGenerator(
          new StandardPieToolTipGenerator(
            StandardPieSectionLabelGenerator.DEFAULT_SECTION_LABEL_FORMAT
          ) 
        );  
      }
    }
    if (useImap()) {
      plot.setInsets(new RectangleInsets(0, 5, 5, 5));
      plot.setURLGenerator(new WikiPieURLGenerator(context));
    }
    return plot;
  }
}
