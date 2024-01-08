package org.stempz.fanime.animeservice.validator;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.stempz.fanime.animeservice.dto.AnimeSaveDto;
import org.stempz.fanime.animeservice.exception.AiredEpisodesBeforeReleaseException;
import org.stempz.fanime.animeservice.exception.AiredEpisodesExceedTotalEpisodesException;
import org.stempz.fanime.animeservice.exception.EndDateBeforeStartDateException;
import org.stempz.fanime.animeservice.exception.EndYearBeforeSeasonYearException;
import org.stempz.fanime.animeservice.exception.MissingStartDateException;
import org.stempz.fanime.animeservice.exception.NegativeEpisodesException;
import org.stempz.fanime.animeservice.exception.StartYearNotEqualToSeasonYearException;
import org.stempz.fanime.animeservice.exception.StatusValidationException;
import org.stempz.fanime.animeservice.model.enums.Status;

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
