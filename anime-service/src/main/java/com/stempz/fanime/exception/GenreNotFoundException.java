package com.stempz.fanime.exception;

import java.util.List;

public class GenreNotFoundException extends RuntimeException {

  private static final String MESSAGE = "Genre is not found";
  private static final String FORMAT_MESSAGE_ONE_GENRE = "Genre with name \"%s\" is not found";
  private static final String FORMAT_MESSAGE_LIST_OF_GENRE = "Genres with names %s are not found";

  public GenreNotFoundException() {
    super(MESSAGE);
  }

  public GenreNotFoundException(String name) {
    super(String.format(FORMAT_MESSAGE_ONE_GENRE, name));
  }

  public GenreNotFoundException(List<String> names) {
    super(String.format(FORMAT_MESSAGE_LIST_OF_GENRE, names));
  }
}
