package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.util.TableOrder;
import org.nascif.jspwiki.plugin.config.Customizer;

public class MultiplePiePlotCustomizer extends Customizer {
  
  public MultiplePiePlotCustomizer() {
    this(MultiplePiePlot.class);
  }

  private MultiplePiePlotCustomizer(Class clazz) {
    super(clazz);
    registerParameterHandler("setDataExtractOrder", TableOrder.class);
    registerParameterHandler("setLimit", double.class);
  }
}
