/*

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
package org.nascif.jspwiki.plugin.imagegen;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.RecoverableChartReaderException;

import com.ecyrd.jspwiki.FileUtil;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.attachment.Attachment;
import com.ecyrd.jspwiki.attachment.AttachmentManager;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.ecyrd.jspwiki.plugin.PluginManager;
import com.ecyrd.jspwiki.plugin.WikiPlugin;
import com.ecyrd.jspwiki.providers.ProviderException;
import com.ecyrd.jspwiki.providers.WikiPageProvider;


/**
 * <code>ImageGeneratorPlugin</code> is the base class for the ImageGen family
 * of JSPWiki plugins. It is *heavily* based on the GraphVizPlugin created by
 * John Volkar - basically a refactoring of it to extract the attachment
 * management engine he created but generalize the application or library used
 * to render the images.
 *
 * This class reads the graph plugin parameters and calls on the concrete
 * implementation to (re)generate a graph and image map which are attached to
 * the specified output page.
 *
 * Three files per graph are generated and attached to a target page using the
 * title as their name (e.g. my-title.jpg, my-title.dat my-title.map). The map
 * file will only be generated if the imap parameter is 'true' AND if it is
 * supported by the specific plugin.
 *
 * When the plugin is invoked it looks at the current page version number and
 * the version number stored in the .dat file, if the numbers do not match (or
 * the .dat file does not exist) the graph is generated.
 * <p>
 * <b>NOTE:</b> This functionality *only* works with the
 * <code>VersioningFileProvider</code> JSPWiki page provider.
 *
 * Generation is delegated to the concrete subclass by means of the
 * <code>generateXXX</code> methods.  This class will attach the resulting image
 * and map files to the target page, creating a .dat file to hold the current
 * version number and graph data as sent to the associatated image generation
 * library or application.
 *
 * <b>Caution</b>: If you specify name=foo, page=baz to more than one plugin
 * invokation, this will cause 'cross-talk' and they will regenerate over each
 * other.
 *
 * Besides the subclass specific parameters, the following parameters are supported:
 * <ul>
 *  <li>title required, the name of the graph.</li>
 *  <li>imap optional, defaults to true, set to false to not have an imap generated (not supported in current version).</li>
 *  <li>page optional, defaults to the current page and is the page to which the generated data is
 * attached.</li>
 *  <li>output</li> optional, the type of the generated image, default is "png".
 * </ul>
 * _body required, contains the graph data. The syntax will depend on the subclass used.
 */
public abstract class ImageGeneratorPlugin implements WikiPlugin {
  private static final String IMAP_EXTENSION  = "imap";
  private static final String GRAPH_EXTENSION = "graph";
  private static final String DAT_EXTENSION   = "dat";

  private static final String PARAM_TITLE  = "title";
  private static final String PARAM_OUTPUT = "output";
  private static final String PARAM_PAGE   = "page";
  private static final String PARAM_IMAP   = "imap";
  private static final String PARAM_WIDTH   = "width";
  private static final String PARAM_HEIGHT   = "height";
  private static final String PARAM_TOOLTIPS = "tooltips";
  private static final String PARAM_SOURCE = "source";
  private static final String PARAM_DEBUG = "debug";

  public static final String PLUGIN_MARKER = "[{";

  protected static final Logger log = Logger.getLogger(ImageGeneratorPlugin.class);
  private static final List CMAP_LIST = Arrays.asList(new String[] {"<map ", "</map>"} );
  private static final String EOL = System.getProperty("line.separator");

  private String m_graphTitle;
  private String m_targetPage;
  private String m_graphData;
  private String m_outputType;
  private boolean m_imap;
  private int m_width;
  private int m_height;
  private boolean m_tooltips;

  private String m_rootName;
  private Attachment m_attachmentSource;
  private boolean m_debug;

  /**
   * Main plugin execution method, read the params, props, regenerate graph if needed, and return
   * image link.
   */
  public final String execute(WikiContext context, Map params) throws PluginException {
    m_rootName = getFileRootName();
    String result = "";
    try {
      if (context.getPage().getVersion() == WikiPageProvider.LATEST_VERSION) {
        reportProblem("This plugin requires a versioned page provider");
      }
      getParams(params, context);
      if (isDataFromBody()) {
        m_graphData = getGraphDataFromBody(context, params);
      } else {
        m_graphData = getGraphDataFromAttachment(context, m_attachmentSource);
      }
      if (isGenerationRequired(context)) {
        generateGraph(context);
      }
      result = generateHtml(context);
	} catch (RecoverableChartReaderException e) {
		// do not rethrow
		result = "<div>Currently, there is insufficient data to render <b>" + m_graphTitle + "</b> chart. As your project evolves, that will automatically appear here.</div>";
	} catch (GraphvizIsNotAvailableException ex) {
		throw new PluginException(ex);
    } catch (Throwable t) {
      result = problemReport(t);
    }
    return result;
  }

  protected String problemReport(Throwable t) {
    String msg = t.toString();
    log.warn(msg, t);

    StringBuffer buf = new StringBuffer();
    buf.append("<p class='error'>").append(m_rootName).append(" failed: ");
    buf.append(msg).append("</p><br/>");
    if (m_debug) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      t.printStackTrace(pw);
      pw.flush();
      sw.flush();
      buf.append("<pre>").append(sw.toString()).append("</pre>");
    }
    return buf.toString();
  }

  //TODO: optimize: copy params and remove found parameters from it as they
  //are processed.
  protected void getParams(Map params, WikiContext context) throws ProviderException, IOException {
    m_debug = getDebug(params);
    m_graphTitle = getGraphTitle(params);
    m_imap = getImap(params);
    m_width = getIntParam(params, PARAM_WIDTH, false, -1);
    m_height = getIntParam(params, PARAM_HEIGHT, false, -1);
    m_tooltips = getToolTips(params);
    m_targetPage = getTargetPage(params, context);
    m_attachmentSource = getAttachmentSource(context, params);
    m_outputType = getOutputType(params);
  }

  private String getGraphTitle(Map params) {
	return getStringParam(params, PARAM_TITLE, true, null);
  }

  private boolean getImap(Map params) {
	boolean imap = getBooleanParam(params, PARAM_IMAP, false, false);
	if (imap && !isImapSupported()) {
	  reportProblem("Image map generation is not supported for this plugin.");
	}
	return imap;
  }

  private boolean getToolTips(Map params) {
	boolean tooltips = getBooleanParam(params, PARAM_TOOLTIPS, false, false);
	if (tooltips && !isImapSupported()) { // tooltips are implemented using HTML imaps
	  reportProblem("Tooltip generation is not supported for this plugin.");
	}
	return tooltips;
  }

  private String getTargetPage(Map params, WikiContext context) {
	return getStringParam(params, PARAM_PAGE, false, context.getPage().getName());
  }

  private Attachment getAttachmentSource(WikiContext context, Map params) throws ProviderException {
    String source = getStringParam(params, PARAM_SOURCE, false, null);
    Attachment attachment = getAttachmentFromName(context, source);
    return attachment;
  }

  private Attachment getAttachmentFromName(WikiContext context, String name) {
	return getAttachmentFromName(context, name, true);
  }

  private Attachment getAttachmentFromName(WikiContext context, String name,
      boolean required)  {
    Attachment attachment = null;
    if (name != null) {
      AttachmentManager attachmentManager = context.getEngine().getAttachmentManager();
      try {
        attachment = attachmentManager.getAttachmentInfo(context, name);
        if (null == attachment && required) {
          reportProblem("Attachment not found for: " + name + " - available: " +
              attachmentManager.listAttachments(context.getPage()).toString());
        }
      } catch (ProviderException e) {
        log.warn(e);
        reportProblem("Error retrieving attachment for: " + name);
      }
    }
    return attachment;
  }

  private String getGraphDataFromBody(WikiContext context, Map params)
    throws PluginException, IOException {
    String graphData = (String) params.get(PluginManager.PARAM_BODY);
    if ((null == graphData) || (graphData.trim().equals(""))) {
      reportProblem("Body parameter must contain graph data.");
    }
    if (graphData.contains(PLUGIN_MARKER)) {
      BodyParser bodyParser = new BodyParser();
      graphData = bodyParser.evalBody(context, graphData);
    }
    return safeTrim(graphData);
  }

  private boolean getDebug(Map params) {
	return getBooleanParam(params, PARAM_DEBUG, false, false);
  }

  private String getOutputType(Map params) {
	return getStringParam(params, PARAM_OUTPUT, false, "png");
  }

  private boolean isGenerationRequired(WikiContext context) throws ProviderException, IOException {
    boolean needed = false;
    if ((null != context) && (null != context.getPage())) {
      String imageName = m_graphTitle + "." + m_outputType;
      Attachment imageAttachment = getAttachmentFromName(context, imageName, false);
      if (null == imageAttachment) {
        needed = true;
        //log.debug("...isGenerationRequired() needed, no image file");
      } else {
        if (isDataFromBody()) {
          needed = isGenerationRequiredForBody(context);
        } else {
          needed = isGenerationRequiredForAttachment(context, imageAttachment);
        }
      }
    }
    return needed;
  }

  /**
   * <code>isGenerationRequiredForAttachment</code> compares the date of the generated
   * image and the source attachment. If the later is more recent, a new image will
   * be generated.
   *
   * @param context
   * @param imageAttachment
   * @return
   */
  private boolean isGenerationRequiredForAttachment(WikiContext context, Attachment imageAttachment) {
    Date sourceDate = m_attachmentSource.getLastModified();
    Date imageDate = imageAttachment.getLastModified();
    boolean needed = sourceDate.after(imageDate);
//    log.debug("...isGenerationRequiredForAttachment() needed: " + needed
//        + " sourceDate: " + sourceDate + "  imageDate:" + imageDate);
	return needed;
  }

  /**
   * <code>isGenerationRequiredForBody</code> compares the contents of the plugin body (after nested plugin
   * evaluation) and the last "body" state as recorded in the .dat attachment. Differences in content
   * will trigger the image generation.
   *
   * @param context
   * @return
   * @throws ProviderException
   * @throws IOException
   */
  private boolean isGenerationRequiredForBody(WikiContext context) throws ProviderException, IOException {
    boolean needed = true;
    // get data from .dat attachment (if present)
    String datName = makeAttachmentName() + ".dat";
    Attachment attachment = getAttachmentFromName(context, datName, false);
    if (attachment == null) {
      log.debug("...isGenerationRequiredForBody() needed, no .dat file");
    } else {
      String lastBody = getGraphDataFromAttachment(context, attachment);
      // compare with body - required if different
      needed = !safeEquals(m_graphData, lastBody);
//      log.debug("...isGenerationRequiredForBody() needed: " + needed);
//      log.debug(m_graphData.length() + " <" + m_graphData + ">");
//      log.debug(lastBody.length() + " <" + lastBody + ">");
	}
	return needed;
  }

  private String makeAttachmentName() {
	return m_targetPage + "/" + m_graphTitle;
  }

  private String getGraphDataFromAttachment(WikiContext context,
	  Attachment attachment) throws ProviderException, IOException {

	InputStream is = getInputStreamFromAttachment(context, attachment);
	String attachmentData = getRawDataFromInputStream(is);
	if (attachmentData.contains(PLUGIN_MARKER)) {
	  reportProblem("Nested plugin processing not supported when using source attachment.");
	}
	if ((null == attachmentData) || (attachmentData.trim().equals(""))) {
	  reportProblem("Unable to read graph data from attachment.");
	}
	return attachmentData;
  }

  private InputStream getInputStreamFromAttachment(WikiContext context, Attachment attachment) throws ProviderException, IOException {
	AttachmentManager manager = context.getEngine().getAttachmentManager();
	return manager.getAttachmentStream(attachment);
  }

  private String getRawDataFromInputStream(InputStream is) throws IOException {
	// Replaced method by Zsolt Koppany; 26-Oct-2008
	try {
	    String attachmentData = FileUtil.readContents(is, "UTF-8");
	    return attachmentData;
	} finally {
		is.close();
	}
//    String attachmentData;
//    try {
//    	attachmentData = ;
//      StringBuffer buf = new StringBuffer();
//      byte[] line = new byte[4096];
//
//      int len = is.available();
//      while (len > 0) {
//        int read = is.read(line, 0, len);
//        buf.append(new String(line, 0, read));
//        len = is.available();
//      }
//      attachmentData = buf.toString().trim();
//    } finally {
//      safeClose(is);
//    }
//    return attachmentData;
  }

  private String getGraphDataFromInputStream(InputStream is, List prefixList) throws IOException {
    String attachmentData;
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(is));
      StringBuffer buf = new StringBuffer();
      String line = reader.readLine();

      while (null != line) {
        boolean add = true;
        for (Iterator iter = prefixList.iterator(); iter.hasNext() && add;) {
          add = !line.startsWith((String)iter.next());
        }
        if (add) {
          buf.append(line).append(EOL);
        }
        line = reader.readLine();
      }
      attachmentData = buf.toString().trim();
    } finally {
      safeClose(reader);
      safeClose(is);
    }
    return attachmentData;
  }

  /**
   * Well, since separate threads *could* be accessing the same page, and since
   * generating graphs may be very expensive, let's limit graph generation to
   * one *thread* at a time. So let's synchronize around a statically held
   * object, and after acquiring it, we'll check again to see if we still need
   * to generate this graph.
   * <p>
   * Why the statics? Well plugin instances are not pooled, so two different
   * instances could be created by the same (or different) threads for the same
   * graph. That's what were guarding against, so using statics give us a
   * communitions channel between all instances of this plugin. Horrid huh?
   * <p>
   * This is HORRID, but works well enough. Not sure how this will act under
   * load, or multiple wiki's.
   *
   * @param context
   */
  private void generateGraph(WikiContext context) {
    synchronized (getGraphGenerationLock()) {
      File graphFile = null;
      File imageFile = null;
      File imapFile = null;
      File datFile = null;

	  try {
		imageFile = createTempFile(m_outputType);
		datFile = createTempFile(DAT_EXTENSION);

		init(context);
		if (isGraphFileSupported()) {
		  graphFile = createTempFile(GRAPH_EXTENSION);

		  // can't rely on this dude being called, and SnipGraphPlugin is!!!
		  generateGraphFile(context, graphFile);
		}

		generateImageFile(context, graphFile, imageFile);
		attachDiskFile(imageFile, m_outputType, context);

		if (useImap() || useToolTips()) {
		  imapFile = createTempFile(IMAP_EXTENSION);
		  generateImapFile(context, graphFile, imapFile);
		  attachDiskFile(imapFile, IMAP_EXTENSION, context);
		}
		if (isDataFromBody()) {
		  generateDatFile(context, datFile);
		  attachDiskFile(datFile, DAT_EXTENSION, context);
		}
	  } catch (RecoverableChartReaderException e) {
		String msg = "Error generating graph - " + e.toString();
		log.warn(msg, e);
		throw e;
	  } catch (Exception e) {
		String msg = "Error generating graph - " + e.toString();
		log.warn(msg, e);
		reportProblem(msg, e);
	  } finally {
		safeDelete(graphFile);
		safeDelete(imageFile);
		safeDelete(imapFile);
		safeDelete(datFile);
		cleanUp(context);
	  }
	  // synch
	}
  }

  private boolean isDataFromBody() {
	return m_attachmentSource == null;
  }

  protected String getImageClass() {
	return "generated_image";
  }

  private File generateDatFile(WikiContext context, File datFile)
						throws IOException {
	FileOutputStream writer = null;

    try {
      writer = new FileOutputStream(datFile);
      // This should be the body content *after* plugin evaluation.
      writer.write(m_graphData.getBytes());
    } finally {
      safeClose(writer);
    }

	return datFile;
  }

  protected void attachDiskFile(File diskFile, String extension, WikiContext context)
                       throws ProviderException, IOException {
    String attachmentName = m_graphTitle + "." + extension;
    Attachment attachment =
        new Attachment(context.getEngine(), m_targetPage, attachmentName);
//    log.info("attaching disk file: " + m_targetPage + "/" + attachmentName);
	FileInputStream fileStream = null;
	try {
	  fileStream = new FileInputStream(diskFile);

	  context.getEngine().getAttachmentManager().
		getCurrentProvider().putAttachmentData(attachment, fileStream);

      // The above bypasses all the reference manager updates!!! But there are REALLY weird
      // reentrancies that occur if you were to instead:
      // context.getEngine().getAttachmentManager().storeAttachment(
    } finally {
      safeClose(fileStream);
    }
  }

  protected String generateHtml(WikiContext context) throws IOException, ProviderException {
    String imageName = makeAttachmentName() + "." + m_outputType;
    String imageUrl =
      context.getEngine().getURL(WikiContext.ATTACH, imageName, null, false);

    StringBuffer html = new StringBuffer(512);
    html.append("<img src='").append(imageUrl).append("' class='");
    html.append(getImageClass()).append("'");
    if (useImap() || useToolTips()) {
      html.append(" USEMAP=\"#").append(m_graphTitle).append("\"");
    }
    if (m_width > 0) {
        html.append(" width='").append(m_width).append("'");
    }
    if (m_height > 0) {
        html.append(" height='").append(m_height).append("'");
    }
    html.append(">\n");
    if (useImap() || useToolTips()) {
      html.append("<map name='").append(StringEscapeUtils.escapeHtml(m_graphTitle)).append("'>\n");
      loadMapData(context, html);
      html.append("</map>\n");
    }
    return html.toString();
  }

  private void loadMapData(WikiContext context, StringBuffer buf) throws ProviderException, IOException {
    Attachment a =
        new Attachment(context.getEngine(), m_targetPage,
            m_graphTitle + "." + IMAP_EXTENSION);

	InputStream is = context.getEngine().getAttachmentManager().getAttachmentStream(a);
	buf.append(getGraphDataFromInputStream(is, CMAP_LIST));
  }

  protected void reportProblem(String msg) {
	throw new ImageGenError(msg);
  }

  protected void reportProblem(String msg, Exception ex) {
	throw new ImageGenError(msg, ex);
  }

  protected String getGraphTitle() {
	return m_graphTitle;
  }

  protected boolean useImap() {
	return m_imap;
  }

  protected boolean useToolTips() {
	return m_tooltips;
  }

  protected String getGraphData() {
	return m_graphData;
  }

  protected String getOutputType() {
	return m_outputType;
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Utility functions

  protected String getFileRootName() {
	String name = getClass().getName();
	int idx = name.lastIndexOf('.');
	if (idx > 0) {
	  name = name.substring(idx + 1);
	}
	return name;
  }

  protected int getIntParam(Map params, String key) {
	return getIntParam(params, key, true, -1);
  }

  protected int getIntParam(Map params, String key, boolean required, int defaultValue) {
	String text = (String) params.get(key);
	int result = 0;

	if ((null == text) || text.trim().equals("")) {
	  if (required) {
		reportProblem(key + " must be specified.");
	  } else {
		result = defaultValue;
	  }
	} else {
	  try {
		result = Integer.parseInt(text);
	  } catch (NumberFormatException e) {
		reportProblem("Invalid value for " + key + ":");
	  }
	}
	return result;
  }

  protected String getStringParam(Map params, String key, boolean required, String defaultValue) {
	String text = (String) params.get(key);
	String result = null;

	if ((null == text)) {
	  if (required) {
		reportProblem(key + " must be specified.");
	  } else {
		result = defaultValue;
	  }
	} else {
	  result = text;
	}
	return result;
  }

  protected boolean getBooleanParam(Map params, String key, boolean required, boolean defaultValue) {
	String text = (String)params.get(key);
	boolean result = false;
	if (null == text) {
	  if (required) {
		reportProblem(key + " must be specified.");
	  } else {
//        log.debug("Boolean param not defined, using default: " + key + " = " + defaultValue);
		result = defaultValue;
	  }
	} else {
	  try {
//        log.debug("Boolean param defined: " + key + " = " + text);
		result = Boolean.valueOf(text).booleanValue();
	  } catch (IllegalArgumentException e) {
		reportProblem("Invalid value for " + key + ":" + text);
	  }
	}
	log.debug("Boolean param: " + key + " = " + result);
	return result;
  }

  protected DateFormat getDateFormatParam(Map params, String key, boolean required, DateFormat defaultValue) {
	String text = (String)params.get(key);
	DateFormat result = null;
	if (null == text) {
	  if (required) {
		reportProblem(key + " must be specified.");
	  } else {
		result = defaultValue;
	  }
	} else {
	  try {
		result = new SimpleDateFormat(text);
	  } catch (IllegalArgumentException e) {
		reportProblem("Invalid value for " + key + ":" + text);
	  }
	}
	return result;
  }

  protected Color getColorParam(Map params, String key, boolean required, Color defaultValue) {
	String text = (String)params.get(key);
	Color result = null;
	if (null == text) {
	  if (required) {
		reportProblem(key + " must be specified.");
	  } else {
		result = defaultValue;
	  }
	} else {
	  try {
		result = new Color(getColorInt(text));
	  } catch (IllegalArgumentException e) {
		reportProblem("Invalid value for " + key + ":" + text);
	  }
	}
	return result;
  }

  //From com.zanthan.sequence.preferences.Prefs
  private int getColorInt(String value) {
    int pos = value.indexOf(' ');
    int red = Integer.parseInt(value.substring(0, pos));
    int lastPos = pos + 1;
    pos = value.indexOf(' ', lastPos);
    int green = Integer.parseInt(value.substring(lastPos, pos));
    lastPos = pos + 1;
    int blue = Integer.parseInt(value.substring(lastPos));

    return (((red * 256) + green) * 256) + blue;
  }

  private File createTempFile(String extension) throws IOException {
	return File.createTempFile(m_rootName, "." + extension);
  }

  protected void safeClose(Reader reader) throws IOException {
    if (null != reader) {
      reader.close();
    }
  }

  protected void safeClose(InputStream is) throws IOException {
    if (null != is) {
      is.close();
    }
  }

  protected void safeClose(Writer writer) throws IOException {
    if (null != writer) {
      writer.close();
    }
  }

  private void safeClose(FileOutputStream writer) throws IOException {
    if (null != writer) {
      writer.close();
    }
  }


  protected void safeDelete(File graphFile) {
	if (null != graphFile) {
	  graphFile.delete();
	}
  }

  protected String safeTrim(String data) {
	if (data != null) {
	  return data.trim();
	}
	return data;
  }

  protected boolean safeEquals(Object data1, Object data2) {
	boolean result = false;
	if (data1 == null && data2 == null) {
	  result = true;
	} else {
	  result = data1.equals(data2);
	}
	return result;
  }

  ////////////////////////////////////////////////////////////////////////////////
  // hooks available for the subclass customization
  protected void init(WikiContext context) {
  }

  protected void cleanUp(WikiContext context) {
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Concrete Subclass  contract
  protected abstract void generateGraphFile(WikiContext context, File graphFile)
	throws Exception;

  protected abstract void generateImageFile(WikiContext context, File graphFile, File imageFile)
	throws Exception;

  protected abstract void generateImapFile(WikiContext context, File graphFile, File imapFile)
	throws Exception;

  protected abstract boolean isImapSupported();

  protected abstract boolean isGraphFileSupported();

  protected abstract Object getGraphGenerationLock();
}
