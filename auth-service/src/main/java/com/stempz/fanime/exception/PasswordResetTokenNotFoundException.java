package com.stempz.fanime.exception;

public class PasswordResetTokenNotFoundException extends RuntimeException {

  private static final String MESSAGE = "Password reset token is not found";

  public PasswordResetTokenNotFoundException() {
    super(MESSAGE);
  }
}
