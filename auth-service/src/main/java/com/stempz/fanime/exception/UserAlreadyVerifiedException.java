package com.stempz.fanime.exception;

public class UserAlreadyVerifiedException extends RuntimeException {

  private static final String MESSAGE = "User is already verified";
  private static final String FORMAT_MESSAGE = "User with username \"%s\" is already verified";

  public UserAlreadyVerifiedException() {
    super(MESSAGE);
  }

  public UserAlreadyVerifiedException(String username) {
    super(String.format(FORMAT_MESSAGE, username));
  }
}
