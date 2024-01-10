package com.stempz.fanime.controller.error;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.stempz.fanime.controller.util.ErrorUtil;
import com.stempz.fanime.dto.ErrorMessageDto;
import com.stempz.fanime.exception.AiredEpisodesBeforeReleaseException;
import com.stempz.fanime.exception.AiredEpisodesExceedTotalEpisodesException;
import com.stempz.fanime.exception.EndDateBeforeStartDateException;
import com.stempz.fanime.exception.EndYearBeforeSeasonYearException;
import com.stempz.fanime.exception.GenreExistsException;
import com.stempz.fanime.exception.GenreNotFoundException;
import com.stempz.fanime.exception.MissingStartDateException;
import com.stempz.fanime.exception.NegativeEpisodesException;
import com.stempz.fanime.exception.StartYearNotEqualToSeasonYearException;
import com.stempz.fanime.exception.StatusValidationException;
import com.stempz.fanime.exception.StudioExistsException;
import com.stempz.fanime.exception.StudioNotFoundException;

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
