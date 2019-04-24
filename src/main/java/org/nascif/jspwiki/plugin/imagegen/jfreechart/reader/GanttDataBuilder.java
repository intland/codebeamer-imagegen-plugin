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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.RegularTimePeriod;

/**
 * A utility class for reading {@link GanttCategoryDataset} data from a
 * structured CSV file or string.  <p/>
 * The format is expected to be: <pre>
 *  :<SERIES NAME>
 *  <TASK NAME>,<TASK START>,<TASK END>[,<PERCENTAGE DONE>]
 *  * <TASK NAME>,<TASK START>,<TASK END>[,<PERCENTAGE DONE>]
 *  ** <TASK NAME>,<TASK START>,<TASK END>[,<PERCENTAGE DONE>]
 *  [:<SERIES NAME> ...]
 * </pre>
 * Note that:
 * <ul>
 *   <li><code>SERIES NAME</code> and <code>TASK NAME</code> are (possibly quoted) strings;</li>
 *   <li><code>TASK START</code> and <code>TASK END</code> are dates in the format specified by the user;</li>
 *   <li><code>PERCENTAGE DONE</code> is a decimal number.
 * </ul>
 * Indentation is used to determine the nesting of tasks and sub-tasks.
 */
public class GanttDataBuilder implements ChartDataBuilder {
  
  public static final int MAX_NESTING_LEVEL = 16; 

  private TaskSeriesCollection dataset;
  private TaskSeries currSeries;
  private Map<String, TaskSeries> seriesMap;
  private Task[] nestingMap = new Task[MAX_NESTING_LEVEL];
  private MetadataList metadata;

  public GanttDataBuilder() {
    dataset = new TaskSeriesCollection();
    seriesMap = new HashMap<String, TaskSeries>();
    metadata = new MetadataList();    
    metadata.add(new StringMetadata("Description"));
    metadata.add(new TimePeriodMetadata("Start", TimePeriodMetadata.DAY));
    metadata.add(new TimePeriodMetadata("End", TimePeriodMetadata.DAY));
    metadata.add(new NumberMetadata("Completed"));
  }

  public void processMetadata(int lineNumber, MetadataList meta) {
    this.metadata = meta;
    if (metadata.size() != 4) {
      throw new ChartReaderException(lineNumber, "Expected four metadata columns, got " + metadata.size());
    }
    if (!(metadata.get(0) instanceof StringMetadata)) {
      throw new ChartReaderException(lineNumber, "First column metadata should be string, was " + metadata.get(0));
    }
    if (!(metadata.get(1) instanceof TimePeriodMetadata)) {
      throw new ChartReaderException(lineNumber, "Second column metadata should be string, was " + metadata.get(1));
    }
    if (!(metadata.get(2) instanceof TimePeriodMetadata)) {
      throw new ChartReaderException(lineNumber, "Third column metadata should be string, was " + metadata.get(2));
    }
    if (!(metadata.get(3) instanceof NumberMetadata)) {
      throw new ChartReaderException(lineNumber, "Fourth column metadata should be string, was " + metadata.get(3));
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
      String seriesName = (String)data.get(0);
      // re-use series if previously named.
      TaskSeries series = (TaskSeries)seriesMap.get(seriesName);
      if (series == null) {
        series = new TaskSeries(seriesName);
        dataset.add(series);
        seriesMap.put(seriesName, series);
      } 
      currSeries = series;
      // TODO: perhaps we should keep a memory of the nesting map?
      Arrays.fill(nestingMap, null); // resets nesting map
    } else {
      List parsedData = metadata.apply(lineNumber, data);
      String name = (String)parsedData.get(0);
      Date start = ((RegularTimePeriod)parsedData.get(1)).getStart();
      Date end = ((RegularTimePeriod)parsedData.get(2)).getStart();
      Task task = new Task(name, start, end);

      // Handles "percent complete" value. 0.0 is ignored, out of range is flagged.
      double percent = ((Double)parsedData.get(3)).doubleValue();
      if (percent > 0 && percent <= 1.0) {
        task.setPercentComplete(percent);
      } else {
        if (percent != 0) {
          throw new ChartReaderException(lineNumber, "Invalid percentage complete number, should be between 0.0 and 1.0, was: " + percent);
        }
      }
      if (level == 1) {
        if (currSeries == null) {
          currSeries = new TaskSeries("Schedule"); // default name for series
          dataset.add(currSeries);
          seriesMap.put("Schedule", currSeries);
        }
        currSeries.add(task);
      } else {
        if (nestingMap[level - 1] == null) {
          throw new ChartReaderException(lineNumber, "Invalid nesting level: " + level);
        }
        nestingMap[level - 1].addSubtask(task);
      }
      nestingMap[level] = task;
    }
  }

  public Dataset getDataset() {
    return dataset;
  }
}
