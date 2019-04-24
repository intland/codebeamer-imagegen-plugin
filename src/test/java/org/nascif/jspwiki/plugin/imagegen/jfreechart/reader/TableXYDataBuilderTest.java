package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import java.io.*;
import junit.framework.*;
import junit.textui.TestRunner;
import org.jfree.data.xy.CategoryTableXYDataset;


/**
 * TableXYDataBuilderTest<p>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class TableXYDataBuilderTest extends TestCase {
  
  private ChartReader reader;

  public TableXYDataBuilderTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TableXYDataBuilderTest.class);
  }

  public static void main (String[] args) {
    TestRunner.run(suite());
  }

  public void setUp() {
    reader = new ChartReader(new TableXYDataBuilder());
  }

  public void tearDown() {
  }

  ///////////////////////////////////////////////////////////////////
  // Test Specific Methods

  public void testSmokeTest() throws Exception {
    String data = 
      ":Index, Score\n" +
      "10,    20\n" + 
      "12,    22\n";
    
    assertNotNull((CategoryTableXYDataset)reader.readDataset(new StringReader(data)));
  }

  public void testSmokeTest2() throws Exception {
    String data = 
      ":Season|Month, ID, Score, Total\n" +
      "12-1990, 0, 20, 30\n" + 
      "01-1991, 1, 22, 32\n";
    
    assertNotNull((CategoryTableXYDataset)reader.readDataset(new StringReader(data)));
  }

  public void testFailNoMetadata() throws Exception {
    String data = 
      "Nascif,    20, 30\n" + 
      "'John Doe',    22, 32\n";
    try {
      reader.readDataset(new StringReader(data));
      fail("Should have failed");
    } catch (ChartReaderException e) {
    }
  }

  public void testFailWrongMetadata() throws Exception {
    String data = 
      ":name|string, Score, Total\n" +
      "Nascif,    20, 30\n" + 
      "'John Doe',    22, 32\n";
    try {
      reader.readDataset(new StringReader(data));
      fail("Should have failed");
    } catch (ChartReaderException e) {
    }
  }

  public void testFailMetadataCountMismatch() throws Exception {
    String data = 
      ":ID, Score, Total\n" +
      "12,    20\n";
    try {
      reader.readDataset(new StringReader(data));
      fail("Should have failed");
    } catch (ChartReaderException e) {
    }
  }

}

