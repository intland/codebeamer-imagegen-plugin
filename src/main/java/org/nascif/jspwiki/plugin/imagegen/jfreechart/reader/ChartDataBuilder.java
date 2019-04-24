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

import java.util.List;
import org.jfree.data.general.Dataset;

/**
 * Describe interface ChartDataListener here.
 *
 *
 * Created: Mon May 16 10:46:49 2005
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 * @version 1.0
 */
public interface ChartDataBuilder {

  /**
   * <code>processMetadata</code> 
   *
   * @param data a <code>List</code> value
   */
  void processMetadata(int line, MetadataList metadata);

  /**
   * <code>processData</code> 
   *
   * @param data a <code>List</code> value
   */
  void processData(int line, int level, List<String> data);

  
  /**
   * <code>getDataset</code> 
   *
   * @return a <code>Dataset</code> value
   */
  Dataset getDataset();
}
