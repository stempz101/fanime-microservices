package org.stempz.fanime.exception;

public class UserAlreadyExistsException extends RuntimeException {

  private static final String MESSAGE = "User already exists";
  private static final String FORMAT_MESSAGE = "User already exists by %s: %s";

  public UserAlreadyExistsException() {
    super(MESSAGE);
  }

  public UserAlreadyExistsException(String field, FieldType fieldType) {
    super(String.format(FORMAT_MESSAGE, fieldType.name().toLowerCase(), field));
  }

  public enum FieldType {
    EMAIL, USERNAME
  }
}
