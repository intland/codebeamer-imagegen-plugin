package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * XYDataBuilderTest<p>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class XYDataBuilderTest extends TestCase {
  
  private ChartReader reader;

  public XYDataBuilderTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(XYDataBuilderTest.class);
  }

  public static void main (String[] args) {
    TestRunner.run(suite());
  }

  public void setUp() {
    reader = new ChartReader(new XYDataBuilder());
  }

  public void tearDown() {
  }

  ///////////////////////////////////////////////////////////////////
  // Test Specific Methods

  public void testSmokeTest() throws Exception {
    String data = 
      "Project\n" + 
      "* 2001, 1.0";
    
    assertNotNull(reader.readDataset(new StringReader(data)));
  }

  public void testSmokeTest3() throws Exception {
    String data = 
      ":ID,Time\n"+ 
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
      ":Sport|Year,Time\n"+ 
      "1998, 21.0\n" +
      "1990,  20.5\n";
    
    try {
      reader.readDataset(new StringReader(data));
      fail("Should have failed");
    } catch (ChartReaderException e) {
    }
  }

  public void testFailLength() throws Exception {
    String data = 
      ":Date,Time,Distance\n"+ 
      "* 31, 21.0\n, 5000" +
      "* 35, 20.5\n, 5000";
    
    try {
      reader.readDataset(new StringReader(data));
      fail("Should have failed");
    } catch (ChartReaderException e) {
    }
  }
  
}

