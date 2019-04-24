package org.nascif.jspwiki.plugin.imagegen;

public class ImageGenError extends Error {

  private static final long serialVersionUID = 1L;

  public ImageGenError(String msg) {
    super(msg);
  }

  public ImageGenError(String msg, Throwable t) {
    super(msg, t);
  }

}
