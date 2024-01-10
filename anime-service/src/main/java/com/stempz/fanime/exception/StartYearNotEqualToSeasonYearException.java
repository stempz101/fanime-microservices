package com.stempz.fanime.exception;

public class StartYearNotEqualToSeasonYearException extends RuntimeException {

  private static final String MESSAGE = "Start year must be equal to the provided season year";

  public StartYearNotEqualToSeasonYearException() {
    super(MESSAGE);
  }
}
