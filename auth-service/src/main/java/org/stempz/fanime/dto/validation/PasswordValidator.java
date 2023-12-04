package org.stempz.fanime.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.stempz.fanime.dto.validation.annotation.Password;

public class PasswordValidator implements ConstraintValidator<Password, char[]> {

  @Override
  public void initialize(Password constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(char[] value, ConstraintValidatorContext context) {
    if (value == null || value.length == 0) {
      setNewErrorMessage("Password must not be empty", context);
      return false;
    }

    String password = new String(value);

    Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    return pattern.matcher(password).matches();
  }

  private void setNewErrorMessage(String message, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }
}
