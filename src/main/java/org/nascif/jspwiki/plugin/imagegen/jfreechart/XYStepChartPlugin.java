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

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.XYPlotCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.MetadataEntry;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.MetadataList;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.NumberMetadata;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.TimePeriodMetadata;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.XYDataBuilder;

import com.ecyrd.jspwiki.WikiContext;

        
public class XYStepChartPlugin extends JFreeChartPlugin
{
  private ValueAxis m_xAxis = null;
  private NumberAxis m_yAxis = null;

  protected void processMetadata(MetadataList metadata) {
    super.processMetadata(metadata);
    boolean usesDateAxis = false;
    if (metadata != null && metadata.size() > 0) {
      MetadataEntry column = metadata.get(0);
      if (column instanceof NumberMetadata) {
        usesDateAxis = false;
      } else {
        if (column instanceof TimePeriodMetadata) {
          usesDateAxis = true;
        } else {
          throw new IllegalArgumentException("Invalid column metadata (must denote number or time period)");
        }
      }
    }
    if (usesDateAxis) {
      m_xAxis = new DateAxis(m_xLabel);
    } else {
      m_xAxis = new NumberAxis(m_xLabel);
    }
    m_yAxis = new NumberAxis(m_xLabel);
    m_yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());    
  }

  protected boolean isImapSupported() {
    return false;
  }

  protected ChartDataBuilder createBuilder() {
    return new XYDataBuilder();
  }

  protected AbstractRenderer createRenderer(WikiContext context) {
    XYToolTipGenerator toolTipGenerator = null;
    if (useToolTips()) {
      toolTipGenerator = new StandardXYToolTipGenerator();
    }
    
    XYURLGenerator urlGenerator = null;
    if (useImap()) {
      urlGenerator = new StandardXYURLGenerator();
    }    
    return new XYStepRenderer(toolTipGenerator, urlGenerator); 
  }

  protected Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer) {
    XYPlot plot = new XYPlot((XYDataset)dataset, m_xAxis, m_yAxis, (XYItemRenderer)renderer);
    plot.setDomainCrosshairVisible(false);
    plot.setRangeCrosshairVisible(false);
    return plot;
  }

  @Override
  protected Customizer createPlotCustomizer() {
    return new XYPlotCustomizer();
  }

}
