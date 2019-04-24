package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

import org.jfree.data.time.RegularTimePeriod;

public class Util {

  private Util() {
  }

  public static Double asDouble(Object value) {
    Double result = null;
    if (value instanceof RegularTimePeriod) {
      result = new Double(((RegularTimePeriod)value).getStart().getTime());
    } else {
      result = (Double)value;
    }
    return result;
  }  
}
