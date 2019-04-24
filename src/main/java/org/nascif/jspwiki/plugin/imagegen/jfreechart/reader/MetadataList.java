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
package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>MetadataList</code> holds a list of <code>MetadataEntry</code>
 * instances that describe the columns of the graph data with their associated
 * names and types.
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class MetadataList {
  
  private List<MetadataEntry> list;

  public MetadataList() {
    list = new ArrayList<MetadataEntry>();
  }

  public void add(MetadataEntry entry) {
    list.add(entry);
  }

  public int size() {
    return list.size();
  }

  public MetadataEntry get(int i) {
    return list.get(i);    
  }

  public List<Object> apply(int lineNumber, List<String> data) {
    List<Object> result = new ArrayList<Object>();
    int len = list.size();
    int dataLen = data == null ? 0 : data.size();
    if (dataLen != len) {
      throw new ChartReaderException(lineNumber, 
                                     "Number of data entries (" + dataLen +
                                     ") don't match number of columns (" + len + 
                                     ").");
    }
    for (int i = 0; i < len; i++) {
      result.add(get(i).parse(lineNumber, (String)data.get(i)));
    }
    return result;
  }

  public boolean equals(Object o) {
    if (o instanceof MetadataList) {
      return list.equals(((MetadataList)o).list);
    }
    return false;
  }

  public int hashCode() {
    return list.hashCode();
  }

  public String toString() {
    return "ML" + list.toString();
  }

}
