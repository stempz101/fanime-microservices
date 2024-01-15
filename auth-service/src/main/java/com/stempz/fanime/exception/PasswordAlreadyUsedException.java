package com.stempz.fanime.exception;

public class PasswordAlreadyUsedException extends RuntimeException {

  private static final String MESSAGE = "Entered new password was previously used or is already in use. Please choose a different password";

  public PasswordAlreadyUsedException() {
    super(MESSAGE);
  }
}
