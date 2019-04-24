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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.data.general.Dataset;

/**
 * There are two kinds of metadata:
 * <pre>
 * - columns name and type for the dataset
 *   >> has to be defined before the dataset is created
 *   >> still can be customized: column names, type (date or number), date format
 *   >> no type implies a number
 *   >> introduced by a marker ':'
 * Example: name1|type1, name1|type2,name3,name4
 * </pre>
 */
public class ChartReader {
  private static Logger log = Logger.getLogger( ChartReader.class );

  public static final char METADATA_MARKER = ':';
  public static final char LEVEL_MARKER = '*';
//  public static final char CUSTOMIZATION_MARKER = '#';

  public static final char METADATA_SEPARATOR = '|';

  public static final char TABLE_DELIMITER = '|';
  public static final char FIELD_DELIMITER = ',';
  public static final char TEXT_DELIMITER  = '\'';

  public static final String STRING_TAG = "STRING";
  public static final String NUMBER_TAG = "NUMBER";

  // the TEXT_DELIMITER can be escaped using this escape character
  public static final char ESCAPE = '\\';

  private ChartDataBuilder builder;
  private int lineNumber;
//  private List<String> customList;
  private MetadataList metadataList = new MetadataList();

  /**
   * Creates a new reader with the specified field and text delimiters.
   *
   * @param fieldDelimiter  the field delimiter (usually a comma, semi-colon,
   *                        colon, tab or space).
   * @param textDelimiter  the text delimiter (usually a single or double
   *                       quote).
   */
  public ChartReader(ChartDataBuilder builder) {
    this.builder = builder;
//    this.customList = new ArrayList<String>();
    this.lineNumber = 0;
  }

  /**
   * Reads a {@link Dataset} from a structured CSV file or input source.
   *
   * @param in  the input source.
   * @return An dataset, as created by the associated builder.
   * @throws IOException if there is an I/O problem.
   */
  public Dataset readDataset(Reader in) throws IOException {

    BufferedReader reader = new BufferedReader(in);
    lineNumber = 0;
    String line = reader.readLine();
    while (line != null) {
      line = line.trim();
      if (line.length() > 0) { // skip empty lines
        switch (line.charAt(0)) {
          case METADATA_MARKER:
            metadataList = parseMetadataLine(line.substring(1));
            builder.processMetadata(lineNumber, metadataList);
            break;
          case TABLE_DELIMITER:
            if (line.charAt(1) == TABLE_DELIMITER) {
              log.debug("Table header, skip: " + line);
            } else {
              List<String> tokens = parseDataLine(line.substring(1), TABLE_DELIMITER);
              builder.processData(lineNumber, 0, tokens);
            }
            break;
          case LEVEL_MARKER:
            int level = 0;
            do {
              level++;
            } while(line.charAt(level) == LEVEL_MARKER);
            List<String> tokens = parseDataLine(line.substring(level));
            builder.processData(lineNumber, level, tokens);
            break;
          default:
            builder.processData(lineNumber, 0, parseDataLine(line));
          }
      }
      line = reader.readLine();
      lineNumber++;
    }
    return builder.getDataset();
  }


  /**
   * <code>getMetadataList</code> returns the last parsed metadata information,
   * or the default one used by the builder.
   *
   * @return a <code>MetadataList</code> value
   */
  public MetadataList getMetadataList() {
    return metadataList;
  }

/*  public List getCustomizationList() {
    return customList;
  }

  private void extractCustomization(String line) {
    customList.add(line);
  }
*/
  final List<String> parseDataLine(String line) {
    return parseDataLine(line, FIELD_DELIMITER);
  }

  final List<String> parseDataLine(String line, final char fieldDelimiter) {
	if (log.isTraceEnabled()) {
		log.trace("parseDataLine -> (" + line + ")");
	}

    List<String> fields = new ArrayList<String>();
    boolean openQuote = false;
    boolean escaped = false;
    StringBuffer field = new StringBuffer();
    for (int i = 0; i < line.length(); i++) {
      char currChar = line.charAt(i);

      if (currChar == ESCAPE) {
    	  if (escaped) {
    		  // double escape!
    		  field.append(currChar);
    	  }
    	  escaped = !escaped;
    	  continue;
      }

      if (currChar == fieldDelimiter && !escaped) {
    	  if (!openQuote) {
    		  fields.add(removeStringDelimiters(field.toString()));
    		  field.setLength(0);
    		  continue;
    	  }
      }
      if (currChar == TEXT_DELIMITER && !escaped) {
    	  openQuote = !openQuote;
    	  continue;
      }

      field.append(currChar);
      escaped = false;
    }
    fields.add(removeStringDelimiters(field.toString()));
    if (openQuote) {
    	throw new ChartReaderException(lineNumber, "Missing delimiter for field " + fields.size());
    }

	if (log.isTraceEnabled()) {
	    log.trace("parseDataLine <- (" + fields + ")");
	}

    return fields;
  }

  MetadataList parseMetadataLine(String line) {
	    MetadataList metadata = new MetadataList();
	    List<String> components = new ArrayList<String>();
	    int start = 0;
	    boolean openQuote = false;
	    for (int i = 0; i < line.length(); i++) {
	      char currChar = line.charAt(i);
	      switch (currChar) {
	        case FIELD_DELIMITER:
	          if (!openQuote) {
	            String field = line.substring(start, i);
	            components.add(removeStringDelimiters(field));
	            start = i + 1;
	          }
	          metadata.add(buildMetadataEntry(components));
	          components.clear();
	          break;
	        case METADATA_SEPARATOR:
	          if (!openQuote) {
	            String field = line.substring(start, i);
	            components.add(removeStringDelimiters(field));
	            start = i + 1;
	          }
	          break;
	        case TEXT_DELIMITER:
	          openQuote = !openQuote;
	          break;
	      }
	    }
	    String field = line.substring(start, line.length());
	    components.add(removeStringDelimiters(field));
	    metadata.add(buildMetadataEntry(components));
	    return metadata;
  }

  MetadataEntry buildMetadataEntry(List components) {
    MetadataEntry result = null;

    int len = components.size();
    if (len == 0) {
      throw new ChartReaderException(lineNumber, "Empty definition for metadata.");
    }
    String name = (String)components.get(0);
    if (len == 1) {
      result = new NumberMetadata(name);
    } else {
      if (len >= 2) {
        String kindName = ((String)components.get(1)).toUpperCase();
        if (kindName.equals(STRING_TAG)) {
          result = new StringMetadata(name);
        } else {
        if (kindName.equals(NUMBER_TAG)) {
          result = new NumberMetadata(name);
        } else { // assume it is a time period
          String periodName = kindName;
          String pattern = null;
          if (len >= 3) {
            pattern = (String)components.get(2);
          }
          try {
            result = new TimePeriodMetadata(name, periodName, pattern);
          } catch (IllegalArgumentException e) {
            throw new ChartReaderException(lineNumber, e.getMessage());
          } } }
      }
    }
    if (result == null) {
      throw new ChartReaderException(lineNumber, "Unable to parse metadata");
    }
    return result;
  }

  /**
   * Removes the string delimiters from a key (as well as any white space
   * outside the delimiters).
   *
   * @param key  the key (including delimiters).
   *
   * @return The key without delimiters.
   */
  String removeStringDelimiters(String field) {
    String k = field.trim();
    if (k.length() == 0) {
      throw new ChartReaderException(lineNumber, "Data contains empty string");
    }
    boolean hasDelimiter = false;
    if (k.charAt(0) == TEXT_DELIMITER) {
      k = k.substring(1);
      hasDelimiter = true;
    }
    if (k.charAt(k.length() - 1) == TEXT_DELIMITER) {
      k = k.substring(0, k.length() - 1);
    } else {
      if (hasDelimiter) {
        throw new ChartReaderException(lineNumber, "Missing delimiter for field " + k);
      }
    }
    return k;
  }

}
