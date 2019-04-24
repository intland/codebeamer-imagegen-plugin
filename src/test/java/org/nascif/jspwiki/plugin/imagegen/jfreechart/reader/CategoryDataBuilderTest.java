package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.*;
import junit.textui.TestRunner;
import org.easymock.MockControl;


/**
 * ChartReaderTest<p>
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class CategoryDataBuilderTest extends TestCase {
  
  private static final String CAT1 = "CAT1";
  private static final String CAT2 = "CAT2";
  private static final String CAT3 = "CAT3";
  private static final String QCAT1 = "Q CAT1";
  private static final String QCAT2 = "Q CAT2";
  private static final String QCAT3 = "Q CAT3";

  private static final String DATA11 = "DATA11";
  private static final String DATA12 = "DATA12";
  private static final String DATA13 = "DATA13";
  private static final String DATA21 = "DATA21";
  private static final String DATA22 = "DATA22";
  private static final String DATA23 = "DATA23";

  private ChartReader reader;
  private MockControl control;
  private ChartDataBuilder builderMock;

  private MetadataList smallMeta, bigMeta;
  private MetadataList smallQuotedMeta;

  public CategoryDataBuilderTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(CategoryDataBuilderTest.class);
  }

  public static void main (String[] args) {
    TestRunner.run(suite());
  }

  public void setUp() {
    control = MockControl.createControl(ChartDataBuilder.class);
    builderMock =  (ChartDataBuilder)control.getMock();
    reader = new ChartReader(builderMock);

    smallMeta = new MetadataList();
    smallMeta.add(new NumberMetadata(CAT1));

    bigMeta = new MetadataList();
    bigMeta.add(new NumberMetadata(CAT1));
    bigMeta.add(new StringMetadata(CAT2));
    bigMeta.add(new TimePeriodMetadata(CAT3, TimePeriodMetadata.MONTH));
    bigMeta.add(new TimePeriodMetadata(CAT3, TimePeriodMetadata.DAY, "dd/mm/yy"));

    smallQuotedMeta = new MetadataList();
    smallQuotedMeta.add(new NumberMetadata(QCAT1));
    smallQuotedMeta.add(new StringMetadata(QCAT2));
    smallQuotedMeta.add(new TimePeriodMetadata(QCAT3, TimePeriodMetadata.MONTH, "mmm yyyy"));
  }

  public void tearDown() {
  }

  ///////////////////////////////////////////////////////////////////
  // Test Specific Methods


  public void testReadCategoryNoEol() throws Exception {
    String data = 
      ":CAT1\n" +
      ":CAT1|Number, CAT2|String, CAT3|Month, CAT3|Day|'dd/mm/yy'";

    builderMock.processMetadata(0, smallMeta);
    builderMock.processMetadata(1, bigMeta);
    builderMock.getDataset();
    control.setReturnValue(null);
    control.replay();
    
    reader.readDataset(new StringReader(data));
    control.verify();
  }

  public void testReadCategoryPadded() throws Exception {
    String data = 
      ":   CAT1 \n" +
      "  :CAT1|Number, CAT2|String, CAT3|Month, CAT3|Day|'dd/mm/yy'\n";
    
    builderMock.processMetadata(0, smallMeta);
    builderMock.processMetadata(1, bigMeta);
    builderMock.getDataset();
    control.setReturnValue(null);
    control.replay();
    
    reader.readDataset(new StringReader(data));
  }

  public void testReadCategorySpaced() throws Exception {
    String data = 
      "   \n" +
      "\n" + 
      ":CAT1\n" +
      "\n" +
      ":CAT1|Number, CAT2|String, CAT3|Month, CAT3|Day|'dd/mm/yy'\n" + 
      "   \n" +
      "\n";

    builderMock.processMetadata(2, smallMeta);
    builderMock.processMetadata(4, bigMeta);
    builderMock.getDataset();
    control.setReturnValue(null);
    control.replay();

    reader.readDataset(new StringReader(data));
    control.verify();
  }

  public void testReadFailedQuotedData() throws Exception {
    String data = 
      ":'CAT1\n";  // no ending quote

    builderMock.processMetadata(0, smallMeta);
    builderMock.getDataset();
    control.setReturnValue(null);
    control.replay();

    try {
      reader.readDataset(new StringReader(data));
      fail("should have failed");
    } catch (ChartReaderException e) {
      // expected;
    }
  }

  public void testReadQuotedCategory() throws Exception {
    String data = 
      ":'Q CAT1', 'Q CAT2'|String, 'Q CAT3'|month|'mmm yyyy'";

    builderMock.processMetadata(0, smallQuotedMeta);
    builderMock.getDataset();
    control.setReturnValue(null);
    control.replay();

    reader.readDataset(new StringReader(data));
    control.verify();
  }

  public void testReadData() throws Exception {
    String data = 
      ":CAT1\n" +
      DATA11 + " , " + DATA12 + "," + DATA13 + "\n" +
      "   " + DATA21 + ", " + DATA22 + "   ," + DATA23 + "\n";

    builderMock.processMetadata(0, smallMeta);
    builderMock.processData(1, 0, Arrays.asList(new String[] { DATA11, DATA12, DATA13 }));
    builderMock.processData(2, 0, Arrays.asList(new String[] { DATA21, DATA22, DATA23 }));
    builderMock.getDataset();
    control.setReturnValue(null);
    control.replay();

    reader.readDataset(new StringReader(data));
    control.verify();
  }

  public void testNestedData() throws Exception {
    String data = 
      /* 0 */":" + CAT1 + "\n" + 
      /* 1 */DATA11 + " , " + DATA12 + "," + DATA13 + "\n" +
      /* 2 */"* " + DATA21 + ", " + DATA22 + "   ," + DATA23 + "\n" +
      /* 3 */"\n" + 
      /* 4 */":" + CAT1 + "\n" + 
      /* 5 */"* " + DATA11 + " , " + DATA12 + ","    + DATA13 + "\n" +
      /* 6 */"*"  + DATA21 + ", "  + DATA22 + "   ," + DATA23 + "\n" +
      /* 7 */"\n" + 
      /* 8 */":" + CAT1 + "\n" + 
      /* 9 */"** "   + DATA11 + " , " + DATA12 + ","    + DATA13 + "\n" +
      /* 10 */"*** " + DATA21 + ", "  + DATA22 + "   ," + DATA23 + "\n";

    builderMock.processMetadata(0, smallMeta);
    builderMock.processData(1, 0, Arrays.asList(new String[] { DATA11, DATA12, DATA13 }));
    builderMock.processData(2, 1, Arrays.asList(new String[] { DATA21, DATA22, DATA23 }));

    builderMock.processMetadata(4, smallMeta);
    builderMock.processData(5, 1, Arrays.asList(new String[] { DATA11, DATA12, DATA13 }));
    builderMock.processData(6, 1, Arrays.asList(new String[] { DATA21, DATA22, DATA23 }));

    builderMock.processMetadata(8, smallMeta);
    builderMock.processData(9, 2, Arrays.asList(new String[] { DATA11, DATA12, DATA13 }));
    builderMock.processData(10, 3, Arrays.asList(new String[] { DATA21, DATA22, DATA23 }));
    builderMock.getDataset();
    control.setReturnValue(null);
    control.replay();

    reader.readDataset(new StringReader(data));
    control.verify();
  }


  public void testBuildMetadataEntry() {
    List<String> components = new ArrayList<String>();

    MetadataEntry defaultMeta = new NumberMetadata(CAT1);
    components.add(CAT1);
    MetadataEntry entry = reader.buildMetadataEntry(components);
    assertEquals("Default metadata error", defaultMeta, entry);
    components.clear();

    MetadataEntry stringMeta = new StringMetadata(CAT1);
    components.add(CAT1); components.add("string");
    entry = reader.buildMetadataEntry(components);
    assertEquals("String metadata error", stringMeta, entry);
    components.clear();

    MetadataEntry numberMeta = new NumberMetadata(CAT1);
    components.add(CAT1);
    entry = reader.buildMetadataEntry(components);
    assertEquals("Number metadata error", numberMeta, entry);
    components.clear();

    MetadataEntry dayMeta = new TimePeriodMetadata(CAT1, TimePeriodMetadata.DAY);
    components.add(CAT1); components.add("DAY");
    entry = reader.buildMetadataEntry(components);
    assertEquals("Day metadata error", dayMeta, entry);
    components.clear();

    String pattern = "MMM-yyyy";
    MetadataEntry monthMeta = new TimePeriodMetadata(CAT1, TimePeriodMetadata.MONTH, pattern);    
    components.add(CAT1); components.add("Month"); components.add(pattern);
    entry = reader.buildMetadataEntry(components);
    assertEquals("Month metadata error", monthMeta, entry);
    components.clear();

    MetadataEntry yearMeta = new TimePeriodMetadata(CAT1, TimePeriodMetadata.YEAR);
    components.add(CAT1); components.add("year");
    entry = reader.buildMetadataEntry(components);
    assertEquals("Year metadata error", yearMeta, entry);
    components.clear();
  }
  
}

