package org.stempz.fanime.controller.error;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.stempz.fanime.dto.ErrorMessageDto;
import org.stempz.fanime.exception.UserAlreadyExistsException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<ErrorMessageDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    log.error("handleMethodArgumentNotValidException: exception {}", ex.getMessage(), ex);
    return ex.getBindingResult().getAllErrors().stream()
        .map(err -> new ErrorMessageDto(err.getDefaultMessage()))
        .collect(Collectors.toList());
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public List<ErrorMessageDto>handleBadCredentialsException(Exception ex) {
    log.error("handleBadCredentialsException: exception {}", ex.getMessage(), ex);
    return List.of(new ErrorMessageDto(ex.getMessage()));
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<ErrorMessageDto> handleUserAlreadyExistsException(Exception ex) {
    log.error("handleUserAlreadyExistsException: exception {}", ex.getMessage(), ex);
    return List.of(new ErrorMessageDto(ex.getMessage()));
  }
}
