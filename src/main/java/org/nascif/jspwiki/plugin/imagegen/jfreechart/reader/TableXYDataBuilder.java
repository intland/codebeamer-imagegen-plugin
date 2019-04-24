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
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.TableXYDataset;

/**
 * A utility class for reading a {@link TableXYDataset} data from a
 * structured CSV file or string.  <p/>
 */
public class TableXYDataBuilder implements ChartDataBuilder {
  
  private CategoryTableXYDataset dataset;
  private MetadataList metadata;

  public TableXYDataBuilder() {
    dataset = new CategoryTableXYDataset();
  }

  public void processMetadata(int lineNumber, MetadataList meta) {
    this.metadata = meta;

    int len = metadata.size();
    if (len < 2) {
      throw new ChartReaderException(lineNumber, "Expected at least two metadata columns, got " + len);
    }
    MetadataEntry column = metadata.get(0);
    if (!(column instanceof NumberMetadata ||
          column instanceof TimePeriodMetadata)) {
      throw new ChartReaderException(lineNumber, 
                                     "Row index (first column) metadata (" + column.getName() 
                                     + ") should be a number or time period.");
    }
    for (int i = 1; i < len; i++) {
      column = metadata.get(i);
      if (!(column instanceof NumberMetadata)) {
        throw new ChartReaderException(lineNumber, 
                                       "Column metadata (" + column.getName() 
                                       + ") should be a number.");
      }
    }
  }

  public void processData(int lineNumber, int level, List<String> data) {
    assert data != null;

    if (metadata == null) {
      throw new ChartReaderException(lineNumber, "Can't process data before metadata is defined");
    }

    if (level != 0) {
      throw new ChartReaderException(lineNumber, "Nesting not supported");
    }

    int numCols = metadata.size();
    if (data.size() != numCols) {
      throw new ChartReaderException(lineNumber, "Number of columns in data (" + 
                                     data.size() + ") must match metadata (" + numCols + ")");
    }
    List parsedData = metadata.apply(lineNumber, data);
    Object rowKey = parsedData.get(0);
    Double rowValue;
    if (rowKey instanceof RegularTimePeriod) {
      rowValue = new Double(((RegularTimePeriod)rowKey).getStart().getTime());
    } else {
      rowValue = (Double)rowKey;
    }
    
    for (int i = 1; i < numCols; i++) {
      String colName = metadata.get(i).getName();
      Double colValue = (Double)parsedData.get(i);
      dataset.add(rowValue, colValue, colName, false);
    }
  }

  public Dataset getDataset() {
    return dataset;
  }
}
