package com.sliit.studentplatform.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper used by ALL controllers.
 *
 * <p>
 * Every endpoint returns {@code ResponseEntity<ApiResponse<T>>} so that
 * the client always receives a consistent JSON shape:
 * 
 * <pre>
 * {
 *   "success": true,
 *   "message": "Group created",
 *   "data": { ... },
 *   "timestamp": "2026-02-22T10:00:00"
 * }
 * </pre>
 *
 * @param <T> the type of the payload ({@code data} field)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private boolean success;
  private String message;
  private T data;
  private LocalDateTime timestamp;

  /**
   * Creates a successful response with data and message.
   *
   * @param data    the response payload
   * @param message a human-readable success message
   * @param <T>     payload type
   * @return a populated {@link ApiResponse}
   */
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a successful response with only a message (no data payload).
   *
   * @param message a human-readable success message
   * @param <T>     payload type
   * @return a populated {@link ApiResponse}
   */
  public static <T> ApiResponse<T> success(String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response.
   *
   * @param message the error description
   * @param <T>     payload type
   * @return a populated error {@link ApiResponse}
   */
  public static <T> ApiResponse<T> error(String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
