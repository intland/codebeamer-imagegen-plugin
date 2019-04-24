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

import org.snipsnap.graph.builder.StringTreeBuilder;
import org.snipsnap.graph.context.GraphRendererContext;
import org.snipsnap.graph.context.RendererContext;
import org.snipsnap.graph.context.UrlContext;
import org.snipsnap.graph.graph.Tree;
import org.snipsnap.graph.renderer.Renderer;

/**
 * Base class for "tree" graphs.<p>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public abstract class TreePlugin extends SnipGraphPlugin {
  
  protected void writeImage(Object data, OutputStream out) throws IOException {
    Tree treeGraph = (Tree)data;
    GraphRendererContext imageContext = new GraphRendererContext();
    Renderer renderer = getTreeRenderer();
    renderer.render(treeGraph, out, imageContext);
  }

  protected void writeImap(Renderer mapRenderer, Object data, OutputStream out) 
    throws IOException {
    RendererContext urlcontext = 
      new UrlContext(getGraphTitle(), // not really used
                     getTreeRenderer());
    //RendererContext imageContext = new GraphRendererContext();
    Tree treeGraph = (Tree)data;
    mapRenderer.render(treeGraph, out, urlcontext);
  }

  protected Object parseData(String body) {
    StringTreeBuilder builder = new StringTreeBuilder(body);
    return builder.build();
  }

  ////////////////////////////////////////////////////////////////////////////////
  // subclass contract
  protected abstract Renderer getTreeRenderer();

} 
