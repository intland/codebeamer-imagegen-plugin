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
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A utility class for reading a {@link TableXYDataset} data from a
 * structured CSV file or string.  <p/>
 */
public class XYDataBuilder implements ChartDataBuilder {
  
  private XYSeriesCollection dataset;
  private XYSeries currSeries;
  private MetadataList metadata;

  public XYDataBuilder() {
    dataset = new XYSeriesCollection();
    metadata = new MetadataList();
    metadata.add(new NumberMetadata("X"));
    metadata.add(new NumberMetadata("Y"));
  }

  public void processMetadata(int lineNumber, MetadataList meta) {
    this.metadata = meta;

    int len = metadata.size();
    if (len != 2) {
      throw new ChartReaderException(lineNumber, "Expected two metadata columns, got " + len);
    }
    for (int i = 0; i < len; i++) {
      MetadataEntry column = metadata.get(i);
      if (!(column instanceof NumberMetadata || 
            column instanceof TimePeriodMetadata)) {
        throw new ChartReaderException(lineNumber, 
                                       "Column metadata (" + column.getName() 
                                       + ") should be a number or time period.");
      }
    }
  }

  public void processData(int lineNumber, int level, List<String> data) {
    assert data != null;

    if (level == 0) {
      if (data.size() != 1) {
        String msg = 
          "Invalid number of entries in series definition, expected 1, was: "+ 
          data.size();
        throw new ChartReaderException(lineNumber, msg);
      }
      currSeries = new XYSeries((String)data.get(0));
      dataset.addSeries(currSeries);
    } else {
      if (level != 1) {
        String msg = 
          "Invalid nesting level in data definition, expected 1, was: "+ 
          level;
        throw new ChartReaderException(lineNumber, msg);
      }
      if (data.size() != 2) {
        String msg = "Invalid number of entries in task definition, should be 2, was: " + 
          data.size();
        throw new ChartReaderException(lineNumber, msg);
      }
      List parsedData = metadata.apply(lineNumber, data);
      Double x = Util.asDouble(parsedData.get(0));
      Double y = Util.asDouble(parsedData.get(1));
      if (currSeries == null) {
        currSeries = new XYSeries("Default"); // default name for series
        dataset.addSeries(currSeries);
      }
      currSeries.add(x, y, false);
    }
  }

  public Dataset getDataset() {
    return dataset;
  }

}
