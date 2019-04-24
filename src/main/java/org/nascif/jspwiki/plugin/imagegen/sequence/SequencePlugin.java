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
package org.nascif.jspwiki.plugin.imagegen.sequence;

import java.io.File;
import java.io.FileWriter;

import org.nascif.jspwiki.plugin.imagegen.ImageGeneratorPlugin;

import com.ecyrd.jspwiki.WikiContext;
import com.zanthan.sequence.headless.PngCreator;

/**
 * <code>SequencePlugin</code> extends the ImageGen framework to add support for
 * UML sequence diagram generation using the SEQUENCE application developed by
 * Alex Moffat (see http://www.zanthan.com/itymbi/archives/cat_sequence.html).
 *
 * <b>NOTE:</b> the current version does not support properties customization.
 *
 * @author <a href="mailto:naabou@wnt.sas.com">Nascif A. Abousalh Neto</a>
 * @version 1.0
 */
public class SequencePlugin extends ImageGeneratorPlugin {

  private static final Object LOCK = new Object();

// integer properties
  String[] intPrefNames = {
    "initial_x_position",
    "initial_y_position",
    "method_execution_width",
    "text_x_pad",
    "text_y_pad",
    "object_lifeline_spacing",
    "method_execution_spacing"
  };

// boolean properties
  String[] booleanPrefNames = {
    "show_return_types"
  };

// color properties
  String[] colorPrefNames = {
    "background_color",
    "link_text_color",
    "link_color",
    "object_border_color",
    "object_fill_color",
    "object_text_color",
    "object_line_color",
    "method_fill_color",
    "method_border_color",
    "selected_fill_color",
    "selected_border_color"
  };

  ////////////////////////////////////////////////////////////////////////////////
  // Default implementations for ImageGeneratorPlugin contract

  protected void generateImageFile(WikiContext context, File graphFile, File imageFile)
    throws Exception {

    boolean useOldParser = false;
    if (getOutputType().equals("png")) {

      log.info("sequence source:" + graphFile.getAbsolutePath());
      PngCreator pngCreator = new PngCreator(graphFile.getAbsolutePath(), 
                                             imageFile.getAbsolutePath(), 
                                             useOldParser);
      pngCreator.output();
      log.info("png file written:" + imageFile.getAbsolutePath());
    } else {
      reportProblem("image output type not supported:" + getOutputType());
    }
  }

  protected void generateImapFile(WikiContext context, File graphFile, File imageFile) {
  }

  protected void generateGraphFile(WikiContext context, File graphFile) 
                            throws Exception {
    FileWriter writer = new FileWriter(graphFile);
    writer.write(getGraphData().trim());
    writer.flush();
    writer.close();
  }

  protected boolean isImapSupported() {
    return false;
  }

  protected boolean isGraphFileSupported() {
    return true;
  }


  ////////////////////////////////////////////////////////////////////////////////
  // Utility functions

// Has to use     public Iterator getPrefs() !*&^%@&%&@@@###!!!


//   protected void getParams(Map params, WikiContext context) {
//     super.getParams(params, context);
//     if (params.size() == 0) {
//       return;
//     }

//     //TODO: optimize: copy params and remove found parameters from it as they
//     //are processed.
//     for (int i = 0; i < intPrefNames.length; i++) {
//       String key = intPrefNames[i];
//       if (params.get(key) != null) {
//         Pref pref = Prefs.getPref(key);
//         int value = getIntParam(params, key, false, pref.getIntegerValue());
//         pref.setIntegerValue(value);
//       }
//     }

//     for (int i = 0; i < booleanPrefNames.length; i++) {
//       String key = booleanPrefNames[i];
//       if (params.get(key) != null) {
//         Pref pref = Prefs.getPref(key);
//         boolean value = getBooleanParam(params, key, false, pref.getBooleanValue());
//         pref.setBooleanValue(value);
//       }
//     }

//     for (int i = 0; i < colorPrefNames.length; i++) {
//       String key = colorPrefNames[i];
//       if (params.get(key) != null) {
//         Pref pref = Prefs.getPref(key);
//         Color value = getColorParam(params, key, false, pref.getColorValue());
//         pref.setColorValue(value);
//       }
//     }
//   }

  protected String getFileRootName() {
    return "SequencePlugin";
  }

  protected Object getGraphGenerationLock() {
    return LOCK;
  }
}
