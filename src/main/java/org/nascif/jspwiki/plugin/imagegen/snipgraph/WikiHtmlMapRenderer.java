/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */
package org.nascif.jspwiki.plugin.imagegen.snipgraph;



import com.ecyrd.jspwiki.WikiContext;
import java.io.*;
import java.util.*;
import org.snipsnap.graph.context.GraphRendererContext;
import org.snipsnap.graph.context.RendererContext;
import org.snipsnap.graph.context.UrlContext;
import org.snipsnap.graph.dimensions.Dim;
import org.snipsnap.graph.graph.Graph;
import org.snipsnap.graph.graph.Node;
import org.snipsnap.graph.renderer.Calculation;
import org.snipsnap.graph.renderer.ImageGraphRenderer;
import org.snipsnap.graph.renderer.Renderer;


public class WikiHtmlMapRenderer extends Calculation implements Renderer {
  private Graph m_graph;
  private Map<String, String[]> m_urlMap;
  private ImageGraphRenderer m_renderer;
  private WikiContext m_wikiContext;

  public WikiHtmlMapRenderer(WikiContext context) {
    m_wikiContext = context;
  }

  public void render(Graph graph, OutputStream out, RendererContext context) {
    m_graph = graph;
    m_renderer = (ImageGraphRenderer) ((UrlContext) context).getRenderer();

    Dim picDim = calculate(m_graph, m_renderer, context); //in Superclass
    ((GraphRendererContext) context).setPicDim(picDim);

    m_urlMap = getUrlMap();

    String mapName = ((UrlContext) (context)).getMapName();
    writeUrlFile(m_urlMap, out, mapName);
  }

  private Map<String, String[]> getUrlMap() {
    m_urlMap = new HashMap<String, String[]>();
    iterate((Node) m_graph.getRoot(), 1);

    return m_urlMap;
  }

  private void writeUrlFile(Map<String, String[]> urlMap, OutputStream out, String mapName) {
    try {
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
      bw.write("<map name=\"" + mapName + "\">");
      bw.newLine();

      Set<String> keys = urlMap.keySet();
      Iterator<String> iterator = keys.iterator();

      while (iterator.hasNext()) {
        String key = iterator.next();
        String href = urlMap.get(key)[0];
        String alt = urlMap.get(key)[1];
        bw.write("<area shape=\"rect\" coords=\"" + key + "\" href=\"" + href +
                 "\" alt=\"" + alt + "\">");
        bw.newLine();
      }

      bw.write("</map>");
      bw.newLine();
      bw.close();
    } catch (IOException ie) {
    }
  }

  private void iterate(Node node, int row) {
    String url = node.getAttribute("url");

    int x1 = node.getX();
    int x2 = node.getX() + node.getWidth();
    int y1 = node.getY();
    int y2 = node.getY() + node.getHeight();
    
    /*if(renderer.getKind()==1){
      y1=y1-node.getHeight()/2;
      y2=y2-node.getHeight()/2;
      }*/
    String coord = "" + x1 + "," + y1 + "," + x2 + "," + y2;
    String nodeName = node.getName();
    String[] str = new String[2];
    if (url == null) {
      url = m_wikiContext.getEngine().getURL(WikiContext.VIEW, nodeName, null, false);
    }
    str[0] = url;
    str[1] = m_wikiContext.getEngine().beautifyTitle(nodeName);
    m_urlMap.put(coord, str);

    ArrayList nodeList = node.getChildrenList();
    Iterator iterator = nodeList.iterator();
    while (iterator.hasNext()) {
      node = (Node) iterator.next();
      iterate(node, row + 1);
    }
  }
}
