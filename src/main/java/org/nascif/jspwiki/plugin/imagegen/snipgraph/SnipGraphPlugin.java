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
package org.nascif.jspwiki.plugin.imagegen.snipgraph;

import com.ecyrd.jspwiki.WikiContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.nascif.jspwiki.plugin.imagegen.ImageGeneratorPlugin;
import org.snipsnap.graph.renderer.Renderer;

/**
 * <code>SnipGraphPlugin</code>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public abstract class SnipGraphPlugin extends ImageGeneratorPlugin {

  private static final Object LOCK = new Object();
  Object parsedData;

  ////////////////////////////////////////////////////////////////////////////////
  // Default implementations for ImageGeneratorPlugin contract

  protected void init(WikiContext context) {
    super.init(context);
    parsedData = parseData(getGraphData().trim());
  }

  protected void generateGraphFile(WikiContext context, File graphFile) {
  }

  protected void generateImageFile(WikiContext context, File graphFile, File imageFile)
    throws Exception {

    if (getOutputType().equals("png")) {
      writeImage(parsedData, new FileOutputStream(imageFile));
    } else {
      reportProblem("image output type not supported:" + getOutputType());
    }
  }

  protected void generateImapFile(WikiContext wikiContext, File graphFile, File imapFile)
    throws Exception {
    Renderer mapRenderer = new WikiHtmlMapRenderer(wikiContext);
    writeImap(mapRenderer, parsedData, new FileOutputStream(imapFile));
  }

  protected boolean isImapSupported() {
    return true;
  }

  protected boolean isGraphFileSupported() {
    return false;
  }

  ////////////////////////////////////////////////////////////////////////////////
  // subclass contract
  ////////////////////////////////////////////////////////////////////////////////
  protected abstract void writeImage(Object data, OutputStream out) 
    throws IOException;

  protected abstract void writeImap(Renderer mapRenderer, Object data, OutputStream out) 
    throws IOException;

  protected abstract Object parseData(String body);
  
  protected Object getGraphGenerationLock() {
    return LOCK;
  }
}
