package org.stempz.fanime.animeservice.controller.error;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.stempz.fanime.animeservice.controller.util.ErrorUtil;
import org.stempz.fanime.animeservice.dto.ErrorMessageDto;
import org.stempz.fanime.animeservice.exception.AiredEpisodesBeforeReleaseException;
import org.stempz.fanime.animeservice.exception.AiredEpisodesExceedTotalEpisodesException;
import org.stempz.fanime.animeservice.exception.EndDateBeforeStartDateException;
import org.stempz.fanime.animeservice.exception.EndYearBeforeSeasonYearException;
import org.stempz.fanime.animeservice.exception.GenreExistsException;
import org.stempz.fanime.animeservice.exception.GenreNotFoundException;
import org.stempz.fanime.animeservice.exception.MissingStartDateException;
import org.stempz.fanime.animeservice.exception.NegativeEpisodesException;
import org.stempz.fanime.animeservice.exception.StartYearNotEqualToSeasonYearException;
import org.stempz.fanime.animeservice.exception.StatusValidationException;
import org.stempz.fanime.animeservice.exception.StudioExistsException;
import org.stempz.fanime.animeservice.exception.StudioNotFoundException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<ErrorMessageDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    log.error("handleMethodArgumentNotValidException: exception {}", ex.getMessage(), ex);
    return ErrorUtil.collectErrorMessagesDto(ex.getBindingResult().getAllErrors());
  }

  @ExceptionHandler({
      AiredEpisodesBeforeReleaseException.class,
      AiredEpisodesExceedTotalEpisodesException.class,
      EndDateBeforeStartDateException.class,
      EndYearBeforeSeasonYearException.class,
      GenreExistsException.class,
      MissingStartDateException.class,
      NegativeEpisodesException.class,
      StartYearNotEqualToSeasonYearException.class,
      StatusValidationException.class,
      StudioExistsException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<ErrorMessageDto> handleBadRequestException(Exception ex) {
    log.error("handleBadRequestException: exception {}", ex.getMessage(), ex);
    return List.of(new ErrorMessageDto(ex.getMessage()));
  }

  @ExceptionHandler({
      GenreNotFoundException.class,
      StudioNotFoundException.class
  })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public List<ErrorMessageDto> handleNotFoundException(Exception ex) {
    log.error("handleNotFoundException: exception {}", ex.getMessage(), ex);
    return List.of(new ErrorMessageDto(ex.getMessage()));
  }
}
