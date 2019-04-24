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
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * A utility class for reading a {@link TimeSeriesCollection} data from a
 * structured CSV file or string.  <p/>
 */
public class TimeSeriesBuilder implements ChartDataBuilder {

  private static final String DEFAULT_SERIES_NAME = "Values";
  private static final Class DEFAULT_SERIES_CLASS = Day.class;

  private TimeSeriesCollection dataset;
  private TimeSeries currSeries;
  private MetadataList metadata;
  private Class timePeriodClass;

  public TimeSeriesBuilder() {
    dataset = new TimeSeriesCollection();
    metadata = new MetadataList();
    // Default: time series for days
    metadata.add(new TimePeriodMetadata("Time", TimePeriodMetadata.DAY));
    metadata.add(new NumberMetadata("Value"));
    timePeriodClass = DEFAULT_SERIES_CLASS;
  }

  public void processMetadata(int lineNumber, MetadataList meta) {
    this.metadata = meta;

    int len = metadata.size();
    if (len != 2) {
      throw new ChartReaderException(lineNumber, "Expected two metadata columns, got " + len);
    }
    MetadataEntry column = metadata.get(0);
    if (!(column instanceof TimePeriodMetadata)) {
      throw new ChartReaderException(lineNumber,
                                     "Column metadata (" + column.getName()
                                     + ") should be a time period.");
    }
    timePeriodClass = ((TimePeriodMetadata)column).getPeriodClass();

    column = metadata.get(1);
    if (!(column instanceof TimePeriodMetadata ||
         column instanceof NumberMetadata)) {
      throw new ChartReaderException(lineNumber,
                                     "Column metadata (" + column.getName()
                                     + ") should be a number or time period.");
    }

    if (currSeries == null) {
    	currSeries = new TimeSeries(metadata.get(0).getName(), timePeriodClass);

	    if (dataset.getSeries() == null || dataset.getSeries().isEmpty()) {
	        dataset.addSeries(currSeries);
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
      currSeries = new TimeSeries((String)data.get(0), timePeriodClass);
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
      RegularTimePeriod x = (RegularTimePeriod)parsedData.get(0);
      Double y = Util.asDouble(parsedData.get(1));
      if (currSeries == null) {
//        currSeries = new TimeSeries(DEFAULT_SERIES_NAME, DEFAULT_SERIES_CLASS);
          currSeries = new TimeSeries(DEFAULT_SERIES_NAME, x.getClass());
          if (dataset.getSeries() == null || dataset.getSeries().isEmpty()) {
              dataset.addSeries(currSeries);
          }
      }
      currSeries.add(x, y);
    }
  }

  public Dataset getDataset() {
    return dataset;
  }

}
