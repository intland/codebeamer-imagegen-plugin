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

import java.io.IOException;
import java.io.OutputStream;

import org.snipsnap.graph.builder.StringUMLBuilder;
import org.snipsnap.graph.context.RendererContext;
import org.snipsnap.graph.context.UMLRendererContext;
import org.snipsnap.graph.graph.uml.UMLGraph;
import org.snipsnap.graph.renderer.Renderer;
import org.snipsnap.graph.renderer.UMLGraphRenderer;

/**
 * <code>UMLGraphPlugin</code>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class UMLGraphPlugin extends SnipGraphPlugin {

  protected void writeImage(Object data, OutputStream out) throws IOException {
    UMLGraph umlGraph = (UMLGraph)data;
    UMLGraphRenderer graphRenderer = new UMLGraphRenderer();
    RendererContext umlContext = new UMLRendererContext();
    graphRenderer.render(umlGraph, out, umlContext);
  }

  protected void writeImap(Renderer mapRenderer, Object data, OutputStream out) 
    throws IOException {
//     RendererContext urlContext = 
//       new UrlContext(getGraphTitle(), // not really used
//                      new UMLGraphRenderer());
//     RendererContext imageContext = new UMLRendererContext();
//     UMLGraph umlGraph = (UMLGraph)data;
//     mapRenderer.render(umlGraph, out, urlContext);
  }

  protected boolean isImapSupported() {
    // SnipGraph currently does not support UML imap generation
    return false;
  }

  protected Object parseData(String body) {
    StringUMLBuilder builder = new StringUMLBuilder(body);
    return builder.build();
  }  
} // UMLGraphPlugin
