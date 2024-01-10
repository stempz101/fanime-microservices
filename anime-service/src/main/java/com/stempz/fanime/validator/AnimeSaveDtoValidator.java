package com.stempz.fanime.validator;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.stempz.fanime.dto.AnimeSaveDto;
import com.stempz.fanime.exception.AiredEpisodesBeforeReleaseException;
import com.stempz.fanime.exception.AiredEpisodesExceedTotalEpisodesException;
import com.stempz.fanime.exception.EndDateBeforeStartDateException;
import com.stempz.fanime.exception.EndYearBeforeSeasonYearException;
import com.stempz.fanime.exception.MissingStartDateException;
import com.stempz.fanime.exception.NegativeEpisodesException;
import com.stempz.fanime.exception.StartYearNotEqualToSeasonYearException;
import com.stempz.fanime.exception.StatusValidationException;
import com.stempz.fanime.model.enums.Status;

@Component
public class AnimeSaveDtoValidator {

  public void validate(AnimeSaveDto animeSaveDto) {
    validateDatesWithSeasonYear(animeSaveDto);
    validateEndDate(animeSaveDto);
    validateEpisodes(animeSaveDto);
    validateStatus(animeSaveDto);
  }

  private void validateDatesWithSeasonYear(AnimeSaveDto animeSaveDto) {
    if (animeSaveDto.getSeasonYear() != null) {
      if (animeSaveDto.getStartDate() != null
          && animeSaveDto.getStartDate().getYear() != animeSaveDto.getSeasonYear()) {
        throw new StartYearNotEqualToSeasonYearException();
      }
      if (animeSaveDto.getEndDate() != null
          && animeSaveDto.getEndDate().getYear() < animeSaveDto.getSeasonYear()) {
        throw new EndYearBeforeSeasonYearException();
      }
    }
  }

  private void validateEndDate(AnimeSaveDto animeSaveDto) {
    if (animeSaveDto.getEndDate() != null && animeSaveDto.getStartDate() != null) {
      if (animeSaveDto.getEndDate().isBefore(animeSaveDto.getStartDate())) {
        throw new EndDateBeforeStartDateException();
      }
    }
  }

  private void validateEpisodes(AnimeSaveDto animeSaveDto) {
    if (animeSaveDto.getTotalEpisodes() < 0 || animeSaveDto.getAiredEpisodes() < 0) {
      throw new NegativeEpisodesException();
    } else if (animeSaveDto.getAiredEpisodes() > animeSaveDto.getTotalEpisodes()) {
      throw new AiredEpisodesExceedTotalEpisodesException();
    } else if (animeSaveDto.getAiredEpisodes() > 0){
      if (animeSaveDto.getStartDate() == null) {
        throw new MissingStartDateException();
      } else if (LocalDate.now().isBefore(animeSaveDto.getStartDate())) {
        throw new AiredEpisodesBeforeReleaseException();
      }
    }
  }

  private void validateStatus(AnimeSaveDto animeSaveDto) {
    if (animeSaveDto.getStatus() != null && !animeSaveDto.getStatus().isEmpty()) {
      try {
        switch (Status.valueOf(animeSaveDto.getStatus().toUpperCase())) {
          case NOT_YET_RELEASED:
            if (animeSaveDto.getAiredEpisodes() != 0) {
              throw new StatusValidationException(Status.NOT_YET_RELEASED);
            }
            break;
          case RELEASING:
            if (animeSaveDto.getAiredEpisodes() <= 0
                || animeSaveDto.getAiredEpisodes() >= animeSaveDto.getTotalEpisodes()) {
              throw new StatusValidationException(Status.RELEASING);
            }
            break;
          case FINISHED:
            if (animeSaveDto.getAiredEpisodes() < animeSaveDto.getTotalEpisodes()) {
              throw new StatusValidationException(Status.FINISHED);
            }
            break;
          case CANCELLED:
          default:
            break;
        }
      } catch (IllegalArgumentException ex) {
        throw new StatusValidationException(animeSaveDto.getStatus());
      }
    }
  }
}
