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
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.Dataset;
import org.nascif.jspwiki.plugin.config.Customizer;
import org.nascif.jspwiki.plugin.imagegen.ImageGeneratorPlugin;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer.RendererCustomizer;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartReader;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.MetadataList;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.providers.ProviderException;

/**
 * Base class for image generator plugins that use the JFreeChart library.
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 * @version 1.0
 */
public abstract class JFreeChartPlugin extends ImageGeneratorPlugin {

  public static final String PARAM_XLABEL = "xaxislabel";
  public static final String PARAM_YLABEL = "yaxislabel";
  public static final String PARAM_HEIGHT = "height";
  public static final String PARAM_WIDTH  = "width";
  public static final String PARAM_LEGEND = "legend";
  public static final String PARAM_THREED = "threed";
  private static final Object LOCK = new Object();

  protected int m_width;
  protected int m_height;
  protected String m_xLabel, m_yLabel;
  protected boolean m_legend, m_3d;

  protected ChartRenderingInfo m_info;

  private Customizer m_plotCustomizer;
  private Customizer m_rendererCustomizer;

  ////////////////////////////////////////////////////////////////////////////////
  // Default implementations for ImageGeneratorPlugin contract
  protected void init(WikiContext context) {
	super.init(context);
	if (useImap() || useToolTips()) {
	  m_info = new ChartRenderingInfo();
	}
  }

  protected void cleanUp(WikiContext context) {
	super.cleanUp(context);
	m_info = null;
  }

  protected void generateImageFile(WikiContext context, File graphFile, File imageFile)
	throws Exception {
	JFreeChart chart = createChart(context);

	// write chart as JPEG file
	if (getOutputType().equals("png")) {
		// CB-SPEC save transparent PNG
		//	  ChartUtilities.saveChartAsPNG(imageFile, chart, m_width, m_height, m_info);
		ChartUtilities.saveChartAsPNG(imageFile, chart, m_width, m_height, m_info, true, 0);
		// CB-SPEC
	  log.info("png file written:" + imageFile.getAbsolutePath());
	} else {
	  if (getOutputType().equals("jpeg")) {
		ChartUtilities.saveChartAsJPEG(imageFile, chart, m_width, m_height, m_info);
		log.info("jpeg file written:" + imageFile.getAbsolutePath());
	  } else {
		reportProblem("image output type not supported:" + getOutputType());
	  }
	}
  }

  protected void generateImapFile(WikiContext context, File graphFile, File imapFile)
	throws IOException {
	if (useImap() || useToolTips()) {
	  ToolTipTagFragmentGenerator toolTipTagFragmentGenerator = null;
	  if (useToolTips()) {
		toolTipTagFragmentGenerator = new StandardToolTipTagFragmentGenerator();
	  }
	  URLTagFragmentGenerator urlTagFragmentGenerator = null;
	  if (useImap()) {
		urlTagFragmentGenerator  = new StandardURLTagFragmentGenerator();
	  }
	  String map = ImageMapUtilities.getImageMap(getGraphTitle(), m_info,
												 toolTipTagFragmentGenerator,
												 urlTagFragmentGenerator);
	  PrintWriter out =
		new PrintWriter(new BufferedWriter(new FileWriter(imapFile)));
	  out.println(map);
	  out.close();
	  log.info("Imap File written:" + imapFile.getAbsolutePath());
	}
  }

  protected void generateGraphFile(WikiContext context, File graphFile) {
  }

  protected boolean isGraphFileSupported() {
	return false;
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Utility functions

  protected void getParams(Map params, WikiContext context) throws ProviderException, IOException {
	super.getParams(params, context);

	m_plotCustomizer = createPlotCustomizer();
	m_plotCustomizer.load(params);

	m_rendererCustomizer = createRendererCustomizer();
	m_rendererCustomizer.load(params);

	m_width  = getWidth(params);
	m_height = getHeight(params);
	m_xLabel = getXAxisLabel(params);
	m_yLabel = getYAxisLabel(params);
	m_legend = getLegend(params);
	m_3d     = get3d(params);
  }

  protected Customizer createRendererCustomizer() {
	return new RendererCustomizer();
  }

  protected abstract Customizer createPlotCustomizer();

  protected void customizeRenderer(AbstractRenderer renderer) {
	m_rendererCustomizer.apply(renderer);
  }

  protected void customizePlot(Plot plot) {
	m_plotCustomizer.apply(plot);
  }

  protected final JFreeChart createChart(WikiContext context) throws Exception {

	ChartDataBuilder dataBuilder = createBuilder();
	ChartReader reader = new ChartReader(dataBuilder);
	Dataset dataset = reader.readDataset(new StringReader(getGraphData()));
	MetadataList metadata = reader.getMetadataList();
	processMetadata(metadata);

	AbstractRenderer renderer = createRenderer(context);
	customizeRenderer(renderer);

	Plot plot = createPlot(context, dataset, renderer);
	customizePlot(plot);

	// CB-SPEC change fonts, soften colors and render transparent background
	//	JFreeChart chart =
	//		  new JFreeChart(getGraphTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, m_legend);
		JFreeChart chart = new JFreeChart(getGraphTitle(), new Font(JFreeChart.DEFAULT_TITLE_FONT.getFamily(), Font.PLAIN, 12), plot, m_legend);
		chart.setBackgroundPaint(new Color(0, 0, 0, 0));
		chart.getTitle().setPaint(new Color(0, 0, 0, 200));
	LegendTitle legend = chart.getLegend();
	if(legend != null) {
		legend.setItemFont(new Font(JFreeChart.DEFAULT_TITLE_FONT.getFamily(), Font.PLAIN, 10));
		legend.setItemPaint(new Color(0, 0, 0, 200));
		legend.setFrame(new BlockBorder(new Color(0, 0, 0, 50)));
	}
	//CB-SPEC

	return chart;
  }

  protected abstract ChartDataBuilder createBuilder();

  // can be overwritten, must be called by subclasses
  protected void processMetadata(MetadataList metadata) {
  }

  protected abstract AbstractRenderer createRenderer(WikiContext context);

  protected abstract Plot createPlot(WikiContext context, Dataset dataset, AbstractRenderer renderer);

  protected abstract boolean isImapSupported();


  protected void printParams(Map params) {
	for (Iterator iter = params.entrySet().iterator(); iter.hasNext();) {
	  Map.Entry entry = (Map.Entry) iter.next();
	  log.info(entry.getKey() + " => " + entry.getValue());
	}
  }

  protected boolean getLegend(Map params) {
	return getBooleanParam(params, PARAM_LEGEND, false, true);
  }

  protected String getXAxisLabel(Map params) {
	return getStringParam(params, PARAM_XLABEL, false, null);
  }

  protected String getYAxisLabel(Map params) {
	return getStringParam(params, PARAM_YLABEL, false, null);
  }

  private int getWidth(Map params) {
	return getIntParam(params, PARAM_WIDTH, false, 500);
  }

  private int getHeight(Map params) {
	return getIntParam(params, PARAM_HEIGHT, false, 300);
  }

  private boolean get3d(Map params) {
	return getBooleanParam(params, PARAM_THREED, false, false);
  }

  protected Object getGraphGenerationLock() {
	return LOCK;
  }
}
