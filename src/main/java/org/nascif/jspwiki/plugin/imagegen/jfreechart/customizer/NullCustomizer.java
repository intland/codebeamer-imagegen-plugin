package org.nascif.jspwiki.plugin.imagegen.jfreechart.customizer;

import java.util.Map;

import org.nascif.jspwiki.plugin.config.Customizer;

public class NullCustomizer extends Customizer {

  public NullCustomizer() {
    super(Object.class);
  }

  public void load(Map params) {
  }

  public void apply(Object target) {
  }
}
