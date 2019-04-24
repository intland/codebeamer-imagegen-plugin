package org.nascif.jspwiki.plugin.imagegen.graphviz;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.nascif.jspwiki.plugin.imagegen.GraphvizIsNotAvailableException;
import org.nascif.jspwiki.plugin.imagegen.ImageGeneratorPlugin;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.providers.ProviderException;

public class GraphVizPlugin extends ImageGeneratorPlugin {
  private static final String NODE_TAG = "node ";
  private static final Object LOCK = new Object();
  private static final String PARAM_FILTER = "filter";
  private String m_executable;
  private String m_graphVizLocation; // from property file...

  private static final String[] FILTERS = { "dot",
                                            "neato",
                                            "twopi",
                                            "circo",
                                            "fdp" };

    protected void getParams(Map params, WikiContext context) throws ProviderException, IOException {
      super.getParams(params, context);
      m_executable = getFilter(params);
      m_graphVizLocation = getGraphVizLocation(context);
    }

    private String getFilter(Map params) {
      String filter = getStringParam(params, PARAM_FILTER, true, null);

      filter = filter.toLowerCase();
      for (int i = 0; i < FILTERS.length; i++) {
        if (FILTERS[i].equals(filter)) {
          return filter;
        }
      }
      reportProblem("filter " + "'" + filter + "' is not valid.");
      return null;
    }

    private String inferGraphType() {
      if (getGraphData().contains("->")) {
        return "digraph";
      } else {
        return "graph";
      }
    }

    private String getGraphVizLocation(WikiContext context) {
      String location = context.getEngine().getWikiProperties().getProperty("graphviz.bin");
      if (location == null) {
    	  location = "";
      } else {
    	  location = location.trim();
      }
      if (location.length() != 0) {
	      File locationDir = new File(location);
	      if (!locationDir.isDirectory()) {
	        reportProblem("graphviz.bin location " + locationDir.getAbsolutePath() + " is not a directory.");
	        return null;
	      }
	      location = locationDir.getAbsolutePath() + File.separatorChar;
      }
      return location;
    }

    @Override
    protected void generateGraphFile(WikiContext context, File graphFile) throws Exception {
      createGraphFile(useImap(), context, graphFile);
    }

    @Override
    protected void generateImageFile(WikiContext context, File graphFile, File imageFile)
    throws Exception {
      spawnGraphViz(graphFile, imageFile, getOutputType());
    }

    @Override
    protected void generateImapFile(WikiContext context, File graphFile, File imapFile) throws Exception {
      spawnGraphViz(graphFile, imapFile, "cmapx");
    }

    @Override
    protected boolean isGraphFileSupported() {
      return true;
    }

    @Override
    protected boolean isImapSupported() {
      return true;
    }

    private Writer getWriter(File file) throws IOException {
    	Writer out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF8");
    	return out;
    }

    private BufferedReader getReader(File file) throws IOException {
    	BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
    	return r;
    }

    private void createGraphFile(boolean inferNodeAttributes, WikiContext context, File graphFile)
    throws IOException {
      String localGraphData = getGraphData();
      Writer writer = null;

      try {
        writer = getWriter(graphFile);
        String graphType = inferGraphType();
        writer.write(graphType);
        writer.write("\n{\n");

        //Output the auto-node attributes before appending the user's data so user data can override.
        if (inferNodeAttributes) {
          String nodeAttributes = generateNodeAttributes(context);
          if (localGraphData.indexOf("$(nodeAttributes)") == -1) {
            writer.write(nodeAttributes);
          } else {
            localGraphData = localGraphData.replaceFirst( "\\$\\(nodeAttributes\\)", nodeAttributes );
          }
        } else {
          localGraphData = localGraphData.replaceFirst( "\\$\\(nodeAttributes\\)", "");
        }
        writer.write(localGraphData);
        writer.write("\n}\n");
      } finally {
        safeClose(writer);
      }
    }

    private String generateNodeAttributes(WikiContext context) throws IOException {
      log.info("generateNodeAttributes");
      // Use graphviz 'plain' output to have it give us the list of nodes in the graph...
      File userGraphFile = null;
      File plainGraphFile = null;
      String nodeAttributes = null;
      try {
        userGraphFile = File.createTempFile("GraphViz", ".user");
        createGraphFile(false, context, userGraphFile);// this is a recursive call...
        plainGraphFile = File.createTempFile("GraphViz", ".plain");
        spawnGraphViz(userGraphFile, plainGraphFile, "plain");
        nodeAttributes = transformNodeAttributes(plainGraphFile, context);
      } finally {
    	  if (!log.isDebugEnabled()) {
	        safeDelete(userGraphFile);
	        safeDelete(plainGraphFile);
    	  }
      }
      return nodeAttributes;
    }

    private String transformNodeAttributes(File plainGraphFile, WikiContext context)
    throws IOException
    {
      StringBuffer buffer = new StringBuffer();
      BufferedReader reader = null;
      try {
        reader = getReader(plainGraphFile);

        String line = reader.readLine();
        while (null != line) {
          if (line.startsWith(NODE_TAG)) {
            String nodeName = line.substring(NODE_TAG.length(),
                line.indexOf(' ', NODE_TAG.length() + 1));
            String prettyName = context.getEngine().beautifyTitle(nodeName);
            String viewUrl = context.getEngine().getViewURL(nodeName);
            buffer.append(nodeName + " [URL=\"" + viewUrl + "\"];\n");
            buffer.append(nodeName + " [label=\"" + prettyName + "\"];\n");
          }
          line = reader.readLine();
        }
      } finally {
        safeClose(reader);
      }
      return buffer.toString();
    }

    private String[] makeCommandLine(File inFile, File outFile, String outputType) {
      String[] cmdArray = new String[5];
      cmdArray[0] = m_graphVizLocation + m_executable;
      cmdArray[1] = "-T" + outputType;
      cmdArray[2] = inFile.getAbsolutePath();
      cmdArray[3] = "-o";
      cmdArray[4] = outFile.getAbsolutePath();
      return cmdArray;
    }

    private void spawnGraphViz(File inFile, File outFile, String outputType)
    throws IOException {
      String[] commandLine = makeCommandLine(inFile, outFile, outputType);

      if (log.isInfoEnabled()) {
    	  StringBuffer buf = new StringBuffer();

    	  for (String cmd : commandLine) {
    		  if (buf.length() != 0) {
    			  buf.append(' ');
    		  }
    		  buf.append('"').append(cmd).append('"');
    	  }
    	  log.info(buf.toString());
      }

      // CB start
      try {
	      Process graphvizProcess = Runtime.getRuntime().exec(commandLine, null, null);
	      try {
	        graphvizProcess.waitFor();
	      } catch (InterruptedException e) {
	        log.warn("InterruptedException in spawnGraphViz.", e);
	        throw new IOException("GraphViz Process was interrupted, see log.");
	      }

	      if (0 != graphvizProcess.exitValue()) {
	        throw new IOException("GraphViz process yeiled non-zero exit value.");
	      }
      } catch (IOException ex) {
    	  throw new GraphvizIsNotAvailableException("This feature requires installing the Graphviz package.", ex);
      }
      // CB end

      log.info("spawnGraphViz completed okay.");
    }


    @Override
    protected Object getGraphGenerationLock() {
      return LOCK;
    }
}
