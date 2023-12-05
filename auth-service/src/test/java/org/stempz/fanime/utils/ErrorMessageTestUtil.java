package org.stempz.fanime.utils;

public class ErrorMessageTestUtil {

  // 400 Bad Request
  public static final String EMAIL_WELL_FORMED = "Email address must be well-formed";
  public static final String EMAIL_NOT_BLANK = "Email must not be empty";
  public static final String USERNAME_NOT_BLANK = "Username must not be empty";
  public static final String PASSWORD_NOT_EMPTY = "Password must not be empty";
  public static final String PASSWORD_INVALID = "Password must contain 8 or more symbols, at least one letter and one digit";
  public static final String USER_EXISTS_FORMAT = "User already exists by %s: %s";

  // 401 Unauthorized
  public static final String BAD_CREDENTIALS = "Bad credentials";
}
