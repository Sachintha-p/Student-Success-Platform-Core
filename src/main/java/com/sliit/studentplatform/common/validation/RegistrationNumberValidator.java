package com.sliit.studentplatform.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation + validator for SLIIT registration numbers.
 *
 * <p>
 * Valid format: {@code IT} followed by exactly 8 digits, e.g.
 * {@code IT21234567}.
 *
 * <p>
 * Usage on DTO fields:
 * 
 * <pre>{@code
 * @ValidRegistrationNumber
 * private String registrationNumber;
 * }</pre>
 */
@Documented
@Constraint(validatedBy = RegistrationNumberValidator.RegistrationNumberConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrationNumberValidator {

  String message() default "Registration number must follow the format IT########  (e.g. IT21234567)";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /** Inner validator implementation. */
  class RegistrationNumberConstraintValidator
      implements ConstraintValidator<RegistrationNumberValidator, String> {

    private static final String REG_NO_PATTERN = "^IT[0-9]{8}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null)
        return true; // let @NotNull handle null checks
      return value.matches(REG_NO_PATTERN);
    }
  }
}
