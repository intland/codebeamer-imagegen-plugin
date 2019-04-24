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
import org.jfree.data.general.DefaultPieDataset;

public class PieDataBuilder implements ChartDataBuilder {
  
  private DefaultPieDataset dataset;
  private MetadataList metadata;

  public PieDataBuilder() {
    dataset = new DefaultPieDataset();
    metadata = new MetadataList();
    metadata.add(new StringMetadata("Category"));
    metadata.add(new NumberMetadata("Value"));
  }

  public void processMetadata(int lineNumber, MetadataList meta) {
    this.metadata = meta;
    if (metadata.size() != 2) {
      throw new ChartReaderException(lineNumber, "Expected two metadata columns, got " + metadata.size());
    }
    if (!(metadata.get(0) instanceof StringMetadata)) {
      throw new ChartReaderException(lineNumber, "First column metadata should be string, was " + metadata.get(0));
    }
    if (!(metadata.get(1) instanceof NumberMetadata)) {
      throw new ChartReaderException(lineNumber, "Second column metadata should be number, was " + metadata.get(1));
    }
  }

  public void processData(int lineNumber, int level, List<String> data) {
    assert data != null;

    if (level != 0) {
      throw new ChartReaderException(lineNumber, "Data nesting not supported");
    }
    List parsedData = metadata.apply(lineNumber, data);
    String name = (String)parsedData.get(0);
    Double value = (Double)parsedData.get(1);
    dataset.setValue(name, value);
  }

  public Dataset getDataset() {
    return dataset;
  }
}
