package org.stempz.fanime.animeservice.exception;

public class EndDateBeforeStartDateException extends RuntimeException {

  public static final String MESSAGE = "End date must be equal to or after the start date";

  public EndDateBeforeStartDateException() {
    super(MESSAGE);
  }
}
