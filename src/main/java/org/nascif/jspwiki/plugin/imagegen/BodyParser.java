package org.nascif.jspwiki.plugin.imagegen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.NoSuchVariableException;
import com.ecyrd.jspwiki.VariableManager;
import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.plugin.PluginException;
import com.ecyrd.jspwiki.plugin.PluginManager;

/**
 *  "Redux" version of the JSPWiki Markup parser to detect and evaluate plugin invocations in a plugin body.
 * Lots of copied code from PluginManager and PluginContent due to private declarations and error "removal".
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 * @version 1.0
 */
public class BodyParser {
  // copied from PluginManager
  protected static final int PUSHBACK_BUFFER_SIZE = 10 * 1024;
  private static Logger log = Logger.getLogger( BodyParser.class );

  public String evalBody(WikiContext context, String graphData)
                  throws PluginException, IOException {
    PushbackReader in = new PushbackReader(new BufferedReader(new StringReader(graphData)),
        PUSHBACK_BUFFER_SIZE);
    boolean quitReading = false;
    StringBuffer buf = new StringBuffer();

    while (!quitReading) {
      int ch = nextToken(in);

      switch (ch) {
      case '[':
          String link = handleOpenBracket(in, buf);
          if (link != null) {
            String result = evalLink(context, link);
            buf.append(result);
          }
          break;

        case -1:
          quitReading = true;
          continue;

        default:
          buf.append((char) ch);
      }
    }

    return buf.toString();
  }

  protected String handleOpenBracket(PushbackReader in, StringBuffer buf) throws IOException {
    StringBuffer sb = new StringBuffer(256);
    int ch;
    boolean isPlugin = false; // plugin or variable...

    while ((ch = nextToken(in)) == '[') {
      sb.append((char) ch);
    }

    if (ch == '{') {
      isPlugin = true;
    }
    pushBack(in, ch);
    if (sb.length() > 0) {
      buf.append(sb);
      return null;
    }

    //  Find end of hyperlink
    ch = nextToken(in);
    int nesting = 1; // Check for nested plugins
    while (ch != -1) {
      int ch2 = nextToken(in);
      pushBack(in, ch2);

      if (isPlugin) {
        if ((ch == '[') && (ch2 == '{')) {
          nesting++;
        } else if ((nesting == 0) && (ch == ']') && (sb.charAt(sb.length() - 1) == '}')) {
          break;
        } else if ((ch == '}') && (ch2 == ']')) {
          // NB: This will be decremented once at the end
          nesting--;
        }
      } else {
        if (ch == ']') {
          break;
        }
      }
      sb.append((char) ch);
      ch = nextToken(in);
    }

    //  If the link is never finished, do some tricks to display the rest of the line
    //  unchanged.
    if (ch == -1) {
      log.debug("Warning: unterminated link detected!");
      buf.append(sb);
      return null;
    }
    return sb.toString();
  }

  //Only plugins and variables are supported; metadata and hyperlinks are not.
  protected String evalLink( WikiContext context, String link ) throws PluginException {
    String content = null;

    WikiEngine engine = context.getEngine();
    if (PluginManager.isPluginLink(link)) {
      content = engine.getPluginManager().execute(context, link);
    } else {
      if (VariableManager.isVariableLink(link)) {
        try {
          content = engine.getVariableManager().parseAndGetValue(context, link);
        } catch (NoSuchVariableException e) {
          throw new PluginException("Undefined variable in plugin body:" + link, e);
        }
      } else {
        content = link;
      }
    }
    return content;
  }

  protected int nextToken(PushbackReader in) throws IOException {
    if (in == null) {
      return -1;
    }
    return in.read();
  }

  /**
   *  Push back any character to the current input.  Does not
   *  push back a read EOF, though.
   */
  protected void pushBack(PushbackReader in, int c) throws IOException {
    if ((c != -1) && (in != null)) {
      in.unread(c);
    }
  }
}
