package org.stempz.fanime.animeservice.exception;

public class NegativeEpisodesException extends RuntimeException {

  private static final String MESSAGE = "Total and aired episodes cannot be negative";

  public NegativeEpisodesException() {
    super(MESSAGE);
  }
}
