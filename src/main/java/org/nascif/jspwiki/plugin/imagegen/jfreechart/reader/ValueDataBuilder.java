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
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.xy.TableXYDataset;

/**
 * A utility class for reading a {@link TableXYDataset} data from a
 * structured CSV file or string.  <p/>
 */
public class ValueDataBuilder implements ChartDataBuilder {
  
  private DefaultValueDataset dataset;
  private MetadataList metadata; 
  
  public ValueDataBuilder() {
    dataset = new DefaultValueDataset();
    metadata = new MetadataList();
    metadata.add(new NumberMetadata("Value"));
  }

  public void processMetadata(int lineNumber, MetadataList meta) {
    this.metadata = meta;

    int len = metadata.size();
    if (len != 1) {
      throw new ChartReaderException(lineNumber, "Expected one metadata column, got " + len);
    }
    MetadataEntry column = metadata.get(0);
    if (!(column instanceof NumberMetadata || 
        column instanceof TimePeriodMetadata)) {
      throw new ChartReaderException(lineNumber, 
          "Column metadata (" + column.getName() 
          + ") should be a number or time period.");
    }
  }

  public void processData(int lineNumber, int level, List<String> data) {
    assert data != null;

    if (level != 0) {
      String msg = 
        "Invalid nesting level in data definition, expected 0, was: "+ 
        level;
      throw new ChartReaderException(lineNumber, msg);
    }
    if (data.size() != 1) {
      String msg = 
        "Invalid number of entries in value definition, expected 1, was: "+ 
        data.size();
      throw new ChartReaderException(lineNumber, msg);
    }
    if (dataset.getValue() != null) {
      throw new ChartReaderException(lineNumber, "Value already defined (only one supported)");
    }
    List parsedData = metadata.apply(lineNumber, data);
    Double value = Util.asDouble(parsedData.get(0));
    dataset.setValue(value);
  }

  public Dataset getDataset() {
    return dataset;
  }

}
