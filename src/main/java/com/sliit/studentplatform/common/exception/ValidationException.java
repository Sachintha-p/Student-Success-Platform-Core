package com.sliit.studentplatform.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown for custom business-rule validation failures (HTTP 422). */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException {

  public ValidationException(String message) {
    super(message);
  }
}
