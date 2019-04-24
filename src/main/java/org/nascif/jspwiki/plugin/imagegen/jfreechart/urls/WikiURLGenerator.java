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



import org.jfree.util.ObjectUtilities;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.parser.MarkupParser;

/**
 * Base class for Wiki URL generators.
 *
 * @author Nascif A. Abousalh-Neto
 */
public class WikiURLGenerator {

  protected WikiContext context;

  public WikiURLGenerator(WikiContext cContext) {
    if (cContext == null) {
      throw new IllegalArgumentException("Null 'context' argument.");
    }

    this.context = cContext;
  }

  public String generateURL(String pageName) {
    assert pageName != null;
    return context.getEngine().getURL(WikiContext.VIEW, pageName, null, false);
  }

  public String generateURL(Comparable pageName, Comparable anchorName) {
    assert pageName != null;
    String page = pageName.toString();
    String url = context.getEngine().getURL(WikiContext.VIEW, page, null, false);
    if (anchorName != null) {
      String anchor = anchorName.toString();
      if (!anchor.equals(page)) {
        url = url + "#section-" + MarkupParser.cleanLink(page) + 
          "-" + MarkupParser.cleanLink(anchor.toString());
      }
    }
    return url;
  }


  /**
   * Tests the generator for equality with an arbitrary object.
   *
   * @param obj  the object (<code>null</code> permitted).
   *
   * @return A boolean.
   */
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof WikiURLGenerator)) {
      return false;
    }

    WikiURLGenerator that = (WikiURLGenerator) obj;

    if (!ObjectUtilities.equal(this.context, that.context)) {
      return false;
    }

    return true;
  }

  /**
   * Returns a hash code.
   *
   * @return A hash code.
   */
  public int hashCode() {
    return 17 + context.hashCode();
  }
}
