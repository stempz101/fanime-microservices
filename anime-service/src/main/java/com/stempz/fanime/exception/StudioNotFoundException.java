package com.stempz.fanime.exception;

public class StudioNotFoundException extends RuntimeException {

  private static final String MESSAGE = "Studio is not found";
  private static final String FORMAT_MESSAGE = "Studio with name \"%s\" is not found";

  public StudioNotFoundException() {
    super(MESSAGE);
  }

  public StudioNotFoundException(String name) {
    super(String.format(FORMAT_MESSAGE, name));
  }
}
