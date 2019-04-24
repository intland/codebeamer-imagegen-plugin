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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jfree.data.time.*;

/**
 * <code>TimePeriodMetadata</code>
 *
 * date format can be tied to the metadata:
 * : Name|<CLASS>|[PATTERN]]
 * 
 * Class if present is one of:
 * <ul>
 *   <li>Year</li>
 *   <li>Quarter</li>
 *   <li>Month</li>
 *   <li>Week</li>
 *   <li>Day</li>
 *   <li>Hour</li>
 *   <li>Minute</li>
 *   <li>Millisecond</li>
 * </ul>
 *
 * Pattern if present follows the <code>SimpleDateFormat</code> convention.
 * Otherwise the associated <code>JFreeChart</code> class parsing method will be
 * used.
 * 
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public class TimePeriodMetadata extends MetadataEntry {

  public static final int YEAR_TAG        = 0;
  public static final int QUARTER_TAG     = 1;
  public static final int MONTH_TAG       = 2;
  public static final int WEEK_TAG        = 3;
  public static final int DAY_TAG         = 4;
  public static final int HOUR_TAG        = 5;
  public static final int MINUTE_TAG      = 6;
  public static final int MILLISECOND_TAG = 7;  


  public static final String YEAR        = "YEAR";
  public static final String QUARTER     = "QUARTER";
  public static final String MONTH       = "MONTH";
  public static final String WEEK        = "WEEK";
  public static final String DAY         = "DAY";
  public static final String HOUR        = "HOUR";
  public static final String MINUTE      = "MINUTE";
  public static final String MILLISECOND = "MILLISECOND";
  
  private int kind = DAY_TAG;
  private SimpleDateFormat format = null;

  public TimePeriodMetadata(String name) {
    this (name, null, null);
  }

  public TimePeriodMetadata(String name, String className) {
    this (name, className, null);
  }

  public TimePeriodMetadata(String name, String className, String pattern) {
    super(name);
    if (className != null) {
      String key = className.toUpperCase();
      if (key.equals("YEAR")) {
        kind = YEAR_TAG;
      } else {
      if (key.equals("QUARTER")) {
        kind = QUARTER_TAG;
      } else {
      if (key.equals("MONTH")) {
        kind = MONTH_TAG;
      } else {
      if (key.equals("WEEK")) {
        kind = WEEK_TAG;
      } else {
      if (key.equals("DAY")) {
        kind = DAY_TAG;
      } else {
      if (key.equals("HOUR")) {
        kind = HOUR_TAG;
      } else {
      if (key.equals("MINUTE")) {
        kind = MINUTE_TAG;
      } else {
      if (key.equals("MILLISECOND")) {
        kind = MILLISECOND_TAG;
      } else {
        throw new IllegalArgumentException("Invalid period type: " + key);
      }}}}}}}}
    }
    if (pattern != null) {
      format = new SimpleDateFormat(pattern);
    }
  }

  public Object parse(int lineNumber, String value) {
    RegularTimePeriod result = null;
    if (format == null) {
      switch (kind) {
        case YEAR_TAG: result = Year.parseYear(value); break;
        case QUARTER_TAG: result = Quarter.parseQuarter(value); break;
        case MONTH_TAG: result = Month.parseMonth(value); break;
        case WEEK_TAG: result = Week.parseWeek(value); break;
        case DAY_TAG: result = Day.parseDay(value); break;
        case HOUR_TAG: result = Hour.parseHour(value); break;
        case MINUTE_TAG: result = Minute.parseMinute(value); break;
        case MILLISECOND_TAG: 
          // there is no 'parseMillisecond'
          Date date = new Date(Long.valueOf(value).longValue());
          result = new Millisecond(date);
          break;
        default:
          assert false: "Undefined kind = " + kind;
      }
      if (result == null) {
        throw new ChartReaderException(lineNumber, "Value can't be parsed as " + 
                                       getPeriodName() +": " + value);
      }
    } else {
      try {
        Date date = format.parse(value);
        switch (kind) {
          case YEAR_TAG: result = new Year(date); break;
          case QUARTER_TAG: result = new Quarter(date); break;
          case MONTH_TAG: result = new Month(date); break;
          case DAY_TAG: result = new Day(date); break;
          case HOUR_TAG: result = new Hour(date); break;
          case MINUTE_TAG: result = new Minute(date); break;
          case MILLISECOND_TAG: result = new Millisecond(date); break;
        }
      } catch (ParseException e) {
        throw new ChartReaderException(lineNumber, "Value can't be parsed as date using pattern \'" + 
                                       format.toPattern() + "' : " + value);
      }
    }
    return result;
  }

  private String getPeriodName() {
    String result = null;
    switch (kind) {
      case YEAR_TAG: result = "Year"; break;
      case QUARTER_TAG: result = "Quarter"; break;
      case MONTH_TAG: result = "Month"; break;
      case WEEK_TAG: result = "Week"; break;
      case DAY_TAG: result = "Day"; break;
      case HOUR_TAG: result = "Hour"; break;
      case MINUTE_TAG: result = "Minute"; break;
      case MILLISECOND_TAG: result = "Millisecond"; break;
      default:
        assert false: "Undefined kind = " + kind;
    }
    return result;
  }

  public Class getPeriodClass() {
    Class result = null;
    switch (kind) {
      case YEAR_TAG: result = Year.class; break;
      case QUARTER_TAG: result = Quarter.class; break;
      case WEEK_TAG: result = Week.class; break;
      case DAY_TAG: result = Day.class; break;
      case HOUR_TAG: result = Hour.class; break;
      case MINUTE_TAG: result = Minute.class; break;
      case MILLISECOND_TAG: result = Millisecond.class; break;
      default:
        assert false;
    }
    return result;
  }

  public boolean equals(Object o) {
    if (o instanceof TimePeriodMetadata) {
      return super.equals(o) &&
        kind == ((TimePeriodMetadata)o).kind &&
        (format == null? ((TimePeriodMetadata)o).format == null :
         format.equals( ((TimePeriodMetadata)o).format));
    }
    return false;
  }

  public int hashCode() {
    return super.hashCode() + (kind * 31) +         
      (format == null ? 17 : format.hashCode());
  }

  public String toString() {
    String result = "TME(" + getName() + "," + kind;
    if (format != null) {
      result =  result + "," + format;
    }
    return result + ")";
  }
  
} // TimePeriodMetadata
