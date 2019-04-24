package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import java.io.*;
import junit.framework.*;
import junit.textui.TestRunner;
import org.jfree.data.general.DefaultPieDataset;


/**
 * PieDataBuilderTest<p>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class PieDataBuilderTest extends TestCase {
  
  private ChartReader reader;

  public PieDataBuilderTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(PieDataBuilderTest.class);
  }

  public static void main (String[] args) {
    TestRunner.run(suite());
  }

  public void setUp() {
    reader = new ChartReader(new PieDataBuilder());
  }

  public void tearDown() {
  }

  ///////////////////////////////////////////////////////////////////
  // Test Specific Methods

  public void testSmokeTest() throws Exception {
    String data = 
      "Low,    20\n" + 
      "Medium,  0\n" + 
      "High,   12\n";
    
    assertNotNull((DefaultPieDataset)reader.readDataset(new StringReader(data)));
  }

  public void testSmokeTest2() throws Exception {
    String data = 
      ":Priority|String, Count|Number\n" + 
      "'Low Priority',    20\n" + 
      "'Medium Priority',  0\n" + 
      "'High Priority',   12\n";
    
    assertNotNull((DefaultPieDataset)reader.readDataset(new StringReader(data)));
  }

}

