package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.jfree.data.time.TimeSeriesCollection;


/**
 * TimeSeriesBuilderTest<p>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class TimeSeriesBuilderTest extends TestCase {
  
  private ChartReader reader;

  public TimeSeriesBuilderTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TimeSeriesBuilderTest.class);
  }

  public static void main (String[] args) {
    TestRunner.run(suite());
  }

  public void setUp() {
    reader = new ChartReader(new TimeSeriesBuilder());
  }

  public void tearDown() {
  }

  ///////////////////////////////////////////////////////////////////
  // Test Specific Methods

  public void testSmokeTest() throws Exception {
    String data = 
      "Project\n" + 
      "* 3/12/2001, 1.0";
    
    assertNotNull((TimeSeriesCollection)reader.readDataset(new StringReader(data)));
  }

  public void testHour() throws Exception {
    String data = 
      ":Hour|Hour,Time\n"+ 
      "Running\n" + 
      "* 2001-12-20 23, 21.0\n" +
      "* 2005-11-20 10, 20.5\n";
    
    assertNotNull(reader.readDataset(new StringReader(data)));
  }

  public void testMinute()  throws Exception {
    String data = 
      ":Minute|Minute,Time\n"+ 
      "Running\n" + 
      "* 2001-12-20:10 24, 21.0\n" +
      "* 2005-11-20:01 10, 20.5\n";
    
    assertNotNull(reader.readDataset(new StringReader(data)));
  }

  public void testMillisecond() throws Exception {
    String data = 
      ":Millisecond|Millisecond,Time\n"+ 
      "Running\n" + 
      "* 123456, 21.0\n" +
      "* 876056, 20.5\n";
    
    assertNotNull(reader.readDataset(new StringReader(data)));
  }

  public void testDay() throws Exception {
    String data = 
      ":Day|DAY,Time\n"+ 
      "Running\n" + 
      "* 1/2/2001, 21.0\n" +
      "* 1/2/2002, 20.5\n" +
      "Rowing\n" + 
      "* 1/2/2001, 21.0\n" +
      "* 1/2/2002, 20.5\n";
    
    assertNotNull(reader.readDataset(new StringReader(data)));
  }

  public void testWeek() throws Exception {
    String data = 
      ":Week|week,Time\n"+ 
      "Running\n" + 
      "* W20-2001, 21.0\n" +
      "* W21-2002, 20.5\n" +
      "Rowing\n" + 
      "* 2001-W10, 21.0\n" +
      "* 2002-W11, 20.5\n";
    
    assertNotNull(reader.readDataset(new StringReader(data)));
  }

  public void testYear() throws Exception {
    String data = 
      ":Year|year,Time\n"+ 
      "Running\n" + 
      "* 2001, 21.0\n" +
      "* 2002, 20.5\n" +
      "Rowing\n" + 
      "* 2001, 21.0\n" +
      "* 2002, 20.5\n";
    
    assertNotNull(reader.readDataset(new StringReader(data)));
  }

  public void testFailType() throws Exception {
    String data = 
      ":Sport|String,Time\n"+ 
      "Running, 21.0\n" +
      "Rowing,  20.5\n";
    
    try {
      reader.readDataset(new StringReader(data));
      fail("Should have failed");
    } catch (ChartReaderException e) {
    }
  }

  public void testFailType2() throws Exception {
    String data = 
      ":Sport,Time\n"+ 
      "1969, 21.0\n" +
      "1970,  20.5\n";
    
    try {
      reader.readDataset(new StringReader(data));
      fail("Should have failed");
    } catch (ChartReaderException e) {
    }
  }

  public void testFailLength() throws Exception {
    String data = 
      ":Date|month,Time,Distance\n"+ 
      "* 3-1992, 21.0\n, 5000" +
      "* 4-1992, 20.5\n, 5000";
    
    try {
      reader.readDataset(new StringReader(data));
      fail("Should have failed");
    } catch (ChartReaderException e) {
    }
  }
  
}

