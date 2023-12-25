package org.stempz.fanime.animeservice.exception;

public class StudioExistsException extends RuntimeException {

  private static final String MESSAGE = "Studio already exists";
  private static final String FORMAT_MESSAGE = "Studio already exists by name: %s";

  public StudioExistsException() {
    super(MESSAGE);
  }

  public StudioExistsException(String name) {
    super(String.format(FORMAT_MESSAGE, name));
  }
}
