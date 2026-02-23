package com.sliit.studentplatform.common.enums;

/**
 * User roles within the Student Success Platform.
 *
 * <p>
 * Used for method-level security with
 * {@code @PreAuthorize("hasRole('ADMIN')")}.
 */
public enum Role {
  STUDENT,
  LECTURER,
  ADMIN
}
