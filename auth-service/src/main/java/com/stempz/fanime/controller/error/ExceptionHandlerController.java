package com.stempz.fanime.controller.error;

import com.stempz.fanime.exception.UserAlreadyVerifiedException;
import com.stempz.fanime.exception.UserNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.stempz.fanime.dto.ErrorMessageDto;
import com.stempz.fanime.exception.UserAlreadyExistsException;

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

  @ExceptionHandler({
      UserAlreadyExistsException.class,
      UserAlreadyVerifiedException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<ErrorMessageDto> handleBadRequestException(Exception ex) {
    log.error("handleBadRequestException: exception {}", ex.getMessage(), ex);
    return List.of(new ErrorMessageDto(ex.getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public List<ErrorMessageDto> handleUserNotFoundException(Exception ex) {
    log.error("handleUserNotFoundException: exception {}", ex.getMessage(), ex);
    return List.of(new ErrorMessageDto(ex.getMessage()));
  }
}
