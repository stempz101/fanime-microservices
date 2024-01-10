package com.stempz.fanime.exception;

public class EndYearBeforeSeasonYearException extends RuntimeException {

  public static final String MESSAGE = "End year must be equal to or after the provided season year";

  public EndYearBeforeSeasonYearException() {
    super(MESSAGE);
  }
}
