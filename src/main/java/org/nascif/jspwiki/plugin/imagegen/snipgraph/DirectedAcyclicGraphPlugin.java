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

import org.snipsnap.graph.builder.StringDirectedAcyclicGraphBuilder;
import org.snipsnap.graph.context.GraphRendererContext;
import org.snipsnap.graph.context.RendererContext;
import org.snipsnap.graph.context.UrlContext;
import org.snipsnap.graph.graph.DirectedGraph;
import org.snipsnap.graph.renderer.DirectedAcyclicGraphRenderer;
import org.snipsnap.graph.renderer.Renderer;

/**
 * <code>DirectedAcyclicGraphPlugin</code>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class DirectedAcyclicGraphPlugin extends SnipGraphPlugin {
  
  public void writeImage(Object data, OutputStream out) {
    DirectedGraph directedGraph = (DirectedGraph)data;

    DirectedAcyclicGraphRenderer graphRenderer = new DirectedAcyclicGraphRenderer();
    RendererContext imageContext = new GraphRendererContext();
    graphRenderer.render(directedGraph, out, imageContext);
  }

  protected void writeImap(Renderer mapRenderer, Object data, OutputStream out) 
    throws IOException {
    RendererContext urlcontext = 
      new UrlContext(getGraphTitle(), // not really used
                     new DirectedAcyclicGraphRenderer());
    //RendererContext imageContext = new GraphRendererContext();
    DirectedGraph directedGraph = (DirectedGraph)data;
    mapRenderer.render(directedGraph, out, urlcontext);

  }

  protected Object parseData(String body) {
    StringDirectedAcyclicGraphBuilder builder = 
      new StringDirectedAcyclicGraphBuilder(body);
    return builder.build();
  }  
  
} // DirectedAcyclicGraphPlugin
