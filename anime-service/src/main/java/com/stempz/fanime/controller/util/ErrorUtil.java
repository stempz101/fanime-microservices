package com.stempz.fanime.controller.util;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.validation.ObjectError;
import com.stempz.fanime.dto.ErrorMessageDto;

@UtilityClass
public class ErrorUtil {

  public List<ErrorMessageDto> collectErrorMessagesDto(List<ObjectError> errors) {
    return errors.stream()
        .map(error -> new ErrorMessageDto(error.getDefaultMessage()))
        .toList();
  }

  public List<ErrorMessageDto> collectErrorMessagesDto(String errorMessage) {
    return List.of(new ErrorMessageDto(errorMessage));
  }
}
