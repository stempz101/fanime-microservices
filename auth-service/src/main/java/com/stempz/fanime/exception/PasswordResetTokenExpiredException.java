package com.stempz.fanime.exception;

public class PasswordResetTokenExpiredException extends RuntimeException {

  private static final String MESSAGE = "Password reset token is already expired";

  public PasswordResetTokenExpiredException() {
    super(MESSAGE);
  }
}
