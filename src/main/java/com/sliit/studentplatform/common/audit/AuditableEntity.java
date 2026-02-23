package com.sliit.studentplatform.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity that provides auditing fields for all domain entities.
 *
 * <p>
 * All entities must extend this class to inherit {@code createdAt},
 * {@code updatedAt}, {@code createdBy}, and {@code updatedBy} fields
 * that are automatically managed by Spring Data JPA Auditing.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditableEntity {

  /**
   * Timestamp when the record was first persisted. Never updated after creation.
   */
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** Timestamp of the most recent update to this record. */
  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /** Username (or system actor) that created the record. */
  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  /** Username (or system actor) that last modified the record. */
  @LastModifiedBy
  @Column(name = "updated_by")
  private String updatedBy;
}
