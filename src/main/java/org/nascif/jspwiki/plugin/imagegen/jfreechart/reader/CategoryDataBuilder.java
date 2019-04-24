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
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

/**
 * A utility class for reading a {@link CategoryDataset} data from a
 * structured CSV file or string.  <p/>
 */
public class CategoryDataBuilder implements ChartDataBuilder {

  private DefaultCategoryDataset m_dataset;
  private MetadataList m_metadata;

  public CategoryDataBuilder() {
	m_dataset = new DefaultCategoryDataset();
  }

  public void processMetadata(int lineNumber, MetadataList metadata) {
	m_metadata = metadata;

	int len = metadata.size();
	if (len < 2) {
	  throw new RecoverableChartReaderException(lineNumber, "Expected at least two metadata columns, got " + len);
	}
	for (int i = 1; i < len; i++) {
	  MetadataEntry column = metadata.get(i);
	  if (!(column instanceof NumberMetadata)) {
		throw new ChartReaderException(lineNumber,
									 "column metadata (" + column.getName()
									 + ") should be a number.");
	  }
	}
  }

  public void processData(int lineNumber, int level, List<String> data) {
	assert data != null;

	if (m_metadata == null) {
	  throw new ChartReaderException(lineNumber, "Can't process data before metadata is defined");
	}

	if (level != 0) {
	  throw new ChartReaderException(lineNumber, "Nesting not supported");
	}

	int numCols = m_metadata.size();
	List parsedData = m_metadata.apply(lineNumber, data);
	Comparable rowKey = (Comparable)parsedData.get(0);
	for (int i = 1; i < numCols; i++) {
	  String colName = m_metadata.get(i).getName();
	  Double value = (Double)parsedData.get(i);
	  m_dataset.addValue(value.doubleValue(), rowKey, colName);
	}
  }

  public Dataset getDataset() {
	return m_dataset;
  }
}
