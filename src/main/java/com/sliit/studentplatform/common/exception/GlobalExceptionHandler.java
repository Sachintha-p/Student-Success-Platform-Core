package com.sliit.studentplatform.common.exception;

import com.sliit.studentplatform.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised exception handler for all REST controllers.
 *
 * <p>
 * Maps domain/infra exceptions to appropriate HTTP status codes and a
 * consistent JSON error envelope.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // ──────────────────────────── 404 ────────────────────────────────────────

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    log.error("ResourceNotFoundException at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
  }

  // ──────────────────────────── 401 ────────────────────────────────────────

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<Map<String, Object>> handleUnauthorized(
      UnauthorizedException ex, HttpServletRequest request) {
    log.error("UnauthorizedException at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    log.error("AccessDeniedException at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildError(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI());
  }

  // ──────────────────────────── 409 ────────────────────────────────────────

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<Map<String, Object>> handleConflict(
      ConflictException ex, HttpServletRequest request) {
    log.error("ConflictException at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildError(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
  }

  // ──────────────────────────── 422 ────────────────────────────────────────

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
        .map((FieldError fe) -> Map.of(
            "field", fe.getField(),
            "message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value"))
        .collect(Collectors.toList());

    Map<String, Object> body = Map.of(
        "success", false,
        "message", "Validation failed",
        "errors", errors,
        "timestamp", LocalDateTime.now().toString(),
        "path", request.getRequestURI());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handleCustomValidation(
      ValidationException ex, HttpServletRequest request) {
    log.error("ValidationException at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI());
  }

  // ──────────────────────────── 500 ────────────────────────────────────────

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneral(
      Exception ex, HttpServletRequest request) {
    log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred. Please try again later.", request.getRequestURI());
  }

  // ─────────────────────────── Helpers ─────────────────────────────────────

  private ResponseEntity<Map<String, Object>> buildError(
      HttpStatus status, String message, String path) {
    Map<String, Object> body = Map.of(
        "success", false,
        "message", message,
        "timestamp", LocalDateTime.now().toString(),
        "path", path);
    return ResponseEntity.status(status).body(body);
  }
}
