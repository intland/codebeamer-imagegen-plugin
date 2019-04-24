/*
 * Copyright by Intland Software
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Intland Software. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Intland.
 */
package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.general.Dataset;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartReader;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartReaderException;
import org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.MetadataList;

import junit.framework.TestCase;

/**
 * Test for ChartReader if parsing the escaped "'"-es will work correctly.
 * @author <a href="mailto:zoltan.luspai@intland.com">Zoltan Luspai</a>
 * $Id$
 */
public class ChartReaderTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * fake ChartDataBuilder just collects the incoming data for testing only.
	 *
	 * @author <a href="mailto:zoltan.luspai@intland.com">Zoltan Luspai</a>
	 * $Id$
	 */
	class TestChartBuilder implements ChartDataBuilder {

		List<List<String>> datalines = new ArrayList<List<String>>();
		List<MetadataList> metadatalines = new ArrayList<MetadataList>();

		/* (non-Javadoc)
		 * @see org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder#getDataset()
		 */
		public Dataset getDataset() {
			return null; // do nothing...
		}

		/* (non-Javadoc)
		 * @see org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder#processData(int, int, java.util.List)
		 */
		public void processData(int line, int level, List<String> data) {
			datalines.add(data);
		}

		/* (non-Javadoc)
		 * @see org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.ChartDataBuilder#processMetadata(int, org.nascif.jspwiki.plugin.imagegen.jfreechart.reader.MetadataList)
		 */
		public void processMetadata(int line, MetadataList metadata) {
			metadatalines.add(metadata);
		}

	}

	/**
	 * Execute the parsing, and return the result data.
	 * @param sampleData
	 * @return
	 * @throws IOException
	 */
	private TestChartBuilder executeParse(String sampleData) throws IOException {
		TestChartBuilder testbuilder = new TestChartBuilder();
		ChartReader chartReader = new ChartReader(testbuilder);
		chartReader.readDataset(new StringReader(sampleData));
		return testbuilder;
	}

	/**
	 * Test with OK data
	 * @throws IOException
	 */
	public void testChartReaderWithOKData() throws IOException {
		String sampleData;
		sampleData = ":Name|String, Start|Day|M/d/yyyy, End|Day|M/d/yyyy, Percentage\n";
		sampleData += "'[ISSUE:3586] Asustek-s ...', 10/22/2008, 11/15/2008, 1.0";

		TestChartBuilder testbuilder = executeParse(sampleData);

		assertEquals("Only one data line", 1, testbuilder.datalines.size());
		List<String> dataline = testbuilder.datalines.get(0);
		assertEqualsDataLine(dataline, new String[] {"[ISSUE:3586] Asustek-s ...", "10/22/2008", "11/15/2008", "1.0"});

		assertMetadata(testbuilder);
	}

	/**
	 * test with data where non-escaped apostrophe inside
	 * @throws IOException
	 */
	public void testChartReaderWithBadEscapedData() throws IOException {
		String sampleData = ":Name|String, Start|Day|M/d/yyyy, End|Day|M/d/yyyy, Percentage\n";
		sampleData += "'[ISSUE:3586] Asustek's ...', 10/22/2008, 11/15/2008, 1.0";

		try {
			executeParse(sampleData);
			fail("Must be an exception");
		} catch (ChartReaderException expected) {
			assertTrue(expected.getMessage(), expected.getMessage().indexOf("Missing delimiter") != -1);
		}
	}

	/**
	 * test with correctly escaped text
	 * @throws IOException
	 */
	public void testChartReaderWithCorrectEscapedData() throws IOException {
		String sampleData = ":Name|String, Start|Day|M/d/yyyy, End|Day|M/d/yyyy, Percentage\n";
		sampleData += "'[ISSUE:3586] Asustek\\'s ...', 10/22/2008, 11/15/2008, 1.0";

		TestChartBuilder testbuilder = executeParse(sampleData);

		assertEquals("Only one data line", 1, testbuilder.datalines.size());
		List<String> dataline = testbuilder.datalines.get(0);
		assertEqualsDataLine(dataline, new String[] {"[ISSUE:3586] Asustek's ...", "10/22/2008", "11/15/2008", "1.0"});

		assertMetadata(testbuilder);
	}

	/**
	 * test double escapes
	 * @throws IOException
	 */
	public void testChartReaderWithDoubleEscapedData() throws IOException {
		String sampleData = ":Name|String, Start|Day|M/d/yyyy, End|Day|M/d/yyyy, Percentage\n";
		sampleData += "'[ISSUE:3586] Asustek\\\\s ...', 10/22/2008, 11/15/2008, 1.0";

		TestChartBuilder testbuilder = executeParse(sampleData);

		assertEquals("Only one data line", 1, testbuilder.datalines.size());
		List<String> dataline = testbuilder.datalines.get(0);
		assertEqualsDataLine(dataline, new String[] {"[ISSUE:3586] Asustek\\s ...", "10/22/2008", "11/15/2008", "1.0"});

		assertMetadata(testbuilder);
	}

	/**
	 * Test when field separator (",") is inside a text value ("'...'")
	 * @throws IOException
	 */
	public void testFieldSeparatorInsideAText() throws IOException {
		String sampleData = ":Name|String, Start|Day|M/d/yyyy, End|Day|M/d/yyyy, Percentage\n";
		sampleData += "'[ISSUE:3586],,, Asustek ...', 10/22/2008, 11/15/2008, 1.0";

		TestChartBuilder testbuilder = executeParse(sampleData);

		assertEquals("Only one data line", 1, testbuilder.datalines.size());
		List<String> dataline = testbuilder.datalines.get(0);
		assertEqualsDataLine(dataline, new String[] {"[ISSUE:3586],,, Asustek ...", "10/22/2008", "11/15/2008", "1.0"});
	}

	/**
	 * Assert that a dataline contains the given strings.
	 * @param dataline
	 * @param strings
	 */
	private void assertEqualsDataLine(List<String> dataline, String[] expected) {
		assertEquals(expected.length, dataline.size());
		for (int i = 0; i < expected.length; i++) {
			String expectedElem = expected[i];
			assertEquals(expectedElem, dataline.get(i));
		}
	}

	/**
	 * Assert that metadata is correct.
	 * @param testbuilder
	 */
	private void assertMetadata(TestChartBuilder testbuilder) {
		assertEquals(1, testbuilder.metadatalines.size());
		MetadataList metadata = testbuilder.metadatalines.get(0);
		assertEquals("Name", metadata.get(0).getName());
		assertEquals("Start", metadata.get(1).getName());
		assertEquals("End", metadata.get(2).getName());
		assertEquals("Percentage", metadata.get(3).getName());
	}

}
