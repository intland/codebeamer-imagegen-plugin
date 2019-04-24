package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import java.util.List;

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.MatrixSeries;
import org.jfree.data.xy.MatrixSeriesCollection;

public class XYZDataBuilder implements ChartDataBuilder {

  private MatrixSeriesCollection dataset;
  private MatrixSeries currSeries;
  private MetadataList metadata;

  public XYZDataBuilder() {
    dataset = new MatrixSeriesCollection();
    metadata = new MetadataList();
    metadata.add(new NumberMetadata("X"));
    metadata.add(new NumberMetadata("Y"));
    metadata.add(new NumberMetadata("Z"));
  }

  public void processMetadata(int lineNumber, MetadataList meta) {
    this.metadata = meta;

    int len = metadata.size();
    if (len != 3) {
      throw new ChartReaderException(lineNumber, "Expected three metadata columns, got " + len);
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
      if (data.size() != 3) {
        String msg = 
          "Invalid number of entries in series definition, expected 3, was: "+ 
          data.size() +
          ":\n  " + data;
        throw new ChartReaderException(lineNumber, msg);
      }
      int numRows = Integer.valueOf(data.get(1));
      int numCols = Integer.valueOf(data.get(2));

      currSeries = new MatrixSeries((String)data.get(0), numRows, numCols);
      dataset.addSeries(currSeries);
    } else {
      if (level != 1) {
        throw new ChartReaderException(lineNumber, 
            "Invalid nesting level in data definition, expected 1, was: "+ 
            level);
      }
      if (currSeries == null) {
        throw new ChartReaderException(lineNumber, "Series must be defined before data");
      }
      if (data.size() != 3) {
        String msg = "Invalid number of entries in task definition, should be 3, was: " + 
          data.size();
        throw new ChartReaderException(lineNumber, msg);
      }
      List parsedData = metadata.apply(lineNumber, data);
      int x = Util.asDouble(parsedData.get(0)).intValue();
      int y = Util.asDouble(parsedData.get(1)).intValue();
      double z = Util.asDouble(parsedData.get(2)).doubleValue();
      currSeries.update(x, y, z);
    }
  }

  public Dataset getDataset() {
    return dataset;
  }

}