package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import java.io.*;
import junit.framework.*;
import junit.textui.TestRunner;
import org.jfree.data.gantt.TaskSeriesCollection;


/**
 * GanttReaderTest<p>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class GanttDataBuilderTest extends TestCase {
  
  private ChartReader reader;

  public GanttDataBuilderTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(GanttDataBuilderTest.class);
  }

  public static void main (String[] args) {
    TestRunner.run(suite());
  }

  public void setUp() {
    reader = new ChartReader(new GanttDataBuilder());
  }

  public void tearDown() {
  }

  ///////////////////////////////////////////////////////////////////
  // Test Specific Methods

  public void testSmokeTest() throws Exception {
    String data = 
      "\"Project\n" + 
      "* \"Write Proposal\", 3/1/2001, 3/5/2001, 1.0";
    
    assertNotNull((TaskSeriesCollection)reader.readDataset(new StringReader(data)));
  }

  public void testSmokeTest2() throws Exception {
    String data = 
      "Project\n" + 
      "* 'Write Proposal', 3/1/2001, 3/5/2001, 1.0\n" +
      "** 'Write Proposal Nested', 3/1/2001, 3/5/2001, 0.5\n" +
      "Testing\n" + 
      "* 'Write Proposal', 3/1/2001, 3/5/2001, 1.0\n" +
      "** 'Write Proposal Nested', 3/1/2001, 3/5/2001, 0.5\n";
    
    assertNotNull((TaskSeriesCollection)reader.readDataset(new StringReader(data)));
  }

}

