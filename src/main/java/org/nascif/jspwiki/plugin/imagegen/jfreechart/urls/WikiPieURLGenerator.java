/* This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 */
package org.nascif.jspwiki.plugin.imagegen.jfreechart.urls;



import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.general.PieDataset;

import com.ecyrd.jspwiki.WikiContext;


/**
 * A URL generator for pie charts.
 *
 * @author Richard Atkinson
 */
public class WikiPieURLGenerator 
 extends WikiURLGenerator 
 implements PieURLGenerator {

  /**
   * Constructor.
   */
  public WikiPieURLGenerator(WikiContext context) {
    super(context);
  }

  /**
   * Generates a URL.
   *
   * @param data  the dataset.
   * @param key  the item key.
   * @param pieIndex  the pie index (ignored).
   *
   * @return A string containing the generated URL.
   */
  public String generateURL(PieDataset data, Comparable key, int pieIndex) {
    return generateURL(key.toString());
  }

  /**
   * Tests if this object is equal to another.
   *
   * @param obj  the object (<code>null</code> permitted).
   *
   * @return A boolean.
   */
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj == this) {
      return true;
    }

    if ((obj instanceof WikiPieURLGenerator) == false) {
      return false;
    }
    return super.equals(obj);
  }
}
