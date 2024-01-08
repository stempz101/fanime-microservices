package org.stempz.fanime.animeservice.exception;

public class MissingStartDateException extends RuntimeException {

  private static final String MESSAGE = "Provide a start date before setting the number of aired episodes";

  public MissingStartDateException() {
    super(MESSAGE);
  }
}
