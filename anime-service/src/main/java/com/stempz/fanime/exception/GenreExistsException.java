package com.stempz.fanime.exception;

public class GenreExistsException extends RuntimeException {

  private static final String MESSAGE = "Genre already exists";
  private static final String FORMAT_MESSAGE = "Genre already exists by name: %s";

  public GenreExistsException() {
    super(MESSAGE);
  }

  public GenreExistsException(String name) {
    super(String.format(FORMAT_MESSAGE, name));
  }
}
