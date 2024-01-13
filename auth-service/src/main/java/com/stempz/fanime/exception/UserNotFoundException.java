package com.stempz.fanime.exception;

import com.stempz.fanime.exception.enums.UserFieldType;

public class UserNotFoundException extends RuntimeException {

  private static final String MESSAGE = "User is not found";
  private static final String FORMAT_MESSAGE = "User is not found by %s: %s";
  private static final String FORMAT_MESSAGE_ID = "User is not found by id: %d";

  public UserNotFoundException() {
    super(MESSAGE);
  }

  public UserNotFoundException(Number id) {
    super(String.format(FORMAT_MESSAGE_ID, id.intValue()));
  }

  public UserNotFoundException(String field, UserFieldType fieldType) {
    super(String.format(FORMAT_MESSAGE, fieldType.name().toLowerCase(), field));
  }
}
