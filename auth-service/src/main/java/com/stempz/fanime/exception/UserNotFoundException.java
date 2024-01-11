package com.stempz.fanime.exception;

public class UserNotFoundException extends RuntimeException {

  private static final String MESSAGE = "User is not found";
  private static final String FORMAT_MESSAGE_ID = "User is not found by id: %d";
  private static final String FORMAT_MESSAGE_TOKEN = "User is not found by token: %s";

  public UserNotFoundException() {
    super(MESSAGE);
  }

  public UserNotFoundException(Number id) {
    super(String.format(FORMAT_MESSAGE_ID, id.intValue()));
  }

  public UserNotFoundException(String token) {
    super(String.format(FORMAT_MESSAGE_TOKEN, token));
  }
}
