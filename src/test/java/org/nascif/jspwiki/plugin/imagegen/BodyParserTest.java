package org.nascif.jspwiki.plugin.imagegen;

import java.io.BufferedReader;
import java.io.PushbackReader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

public class BodyParserTest extends TestCase {

  private BodyParser _bp;
  
  @Before
  public void setUp() throws Exception {
    _bp = new BodyParser();
  }

  @After
  public void tearDown() throws Exception {
  }

  public void testHandleOpenBracketUnterminated() throws Exception {
    String data = "lal al{ alalalala\nk} akakakakak";

    PushbackReader in = new PushbackReader(new BufferedReader(new StringReader(data)));
    StringBuffer buf = new StringBuffer();
    String result = _bp.handleOpenBracket(in, buf);
    
    assertEquals(data, buf.toString());
    assertNull(result);
  }

  public void testHandleOpenBracketWikiLink() throws Exception {
    String filler = "lalalala";
    String data = filler + "]";
    PushbackReader in = new PushbackReader(new BufferedReader(new StringReader(data)));
    StringBuffer buf = new StringBuffer();
    String result = _bp.handleOpenBracket(in, buf);
    
    assertEquals("", buf.toString());
    assertEquals(filler, result);
  }

  public void testHandleOpenBracketNested() throws Exception {
    String filler = "{ ksksksksks [{ kakaksdjdjsd }] }";
    String data = filler + "]";
    PushbackReader in = new PushbackReader(new BufferedReader(new StringReader(data)));
    StringBuffer buf = new StringBuffer();
    String result = _bp.handleOpenBracket(in, buf);
    
    assertEquals("", buf.toString());
    assertEquals(filler, result);
  }

}
