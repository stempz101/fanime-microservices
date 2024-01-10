package com.stempz.fanime.exception;

public class AiredEpisodesBeforeReleaseException extends RuntimeException {

  private static final String MESSAGE = "Aired episodes cannot be set until the anime is released";

  public AiredEpisodesBeforeReleaseException() {
    super(MESSAGE);
  }
}
