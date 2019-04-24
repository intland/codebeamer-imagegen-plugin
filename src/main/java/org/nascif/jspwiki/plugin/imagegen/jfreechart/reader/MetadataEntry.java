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


/**
 * MetadataEntry is the base class for the concrete classes that translate
 * strings to objects according to metadata information defined by the user.
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 */
public abstract class MetadataEntry {
  
  private String name;

  public MetadataEntry(String name) {
    this.name = name;
  } // MetadataEntry constructor

  public String getName() {
    return name;
  }

  public abstract Object parse(int lineNumber, String value);

  public boolean equals(Object o) {
    if (o instanceof MetadataEntry) {
      return name.equals(((MetadataEntry)o).name);
    }
    return false;
  }

  public int hashCode() {
    return name.hashCode();
  }

} // MetadataEntry
