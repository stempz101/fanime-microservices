package com.stempz.fanime.exception;

import java.util.Map;
import com.stempz.fanime.model.enums.Status;

public class StatusValidationException extends RuntimeException {

  private static final String FORMAT_MESSAGE = "Invalid anime status: %s";
  private static final Map<Status, String> MESSAGE_MAP = Map.of(
      Status.FINISHED, "Anime with status 'FINISHED' must have all episodes aired",
      Status.RELEASING, "Anime with status 'RELEASING' should have some episodes aired, and the number of aired episodes should be between 1 and total episodes",
      Status.NOT_YET_RELEASED, "Anime with status 'NOT YET RELEASED' should have no episodes aired"
  );

  public StatusValidationException(String status) {
    super(String.format(FORMAT_MESSAGE, status));
  }

  public StatusValidationException(Status status) {
    super(MESSAGE_MAP.getOrDefault(status, ""));
  }

}
