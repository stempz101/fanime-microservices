package com.stempz.fanime.exception;

public class AiredEpisodesExceedTotalEpisodesException extends RuntimeException {

  private static final String MESSAGE = "Aired episodes cannot exceed total episodes";

  public AiredEpisodesExceedTotalEpisodesException() {
    super(MESSAGE);
  }
}
