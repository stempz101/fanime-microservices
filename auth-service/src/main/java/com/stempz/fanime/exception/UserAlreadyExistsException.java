package com.stempz.fanime.exception;

import com.stempz.fanime.exception.enums.UserFieldType;

public class UserAlreadyExistsException extends RuntimeException {

  private static final String MESSAGE = "User already exists";
  private static final String FORMAT_MESSAGE = "User already exists by %s: %s";

  public UserAlreadyExistsException() {
    super(MESSAGE);
  }

  public UserAlreadyExistsException(String field, UserFieldType fieldType) {
    super(String.format(FORMAT_MESSAGE, fieldType.name().toLowerCase(), field));
  }
}
