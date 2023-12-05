package org.stempz.fanime.dto.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.stempz.fanime.dto.validation.PasswordValidator;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

  String message() default "Password must contain 8 or more symbols, at least one letter and one digit";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
