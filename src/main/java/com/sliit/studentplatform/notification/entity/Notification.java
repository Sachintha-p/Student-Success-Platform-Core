package com.sliit.studentplatform.notification.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/** A notification delivered to a user. */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipient_id", nullable = false)
  private User recipient;

  @Column(nullable = false, length = 100)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String message;

  /** Type: INVITATION | JOIN_REQUEST | EVENT | TASK | SYSTEM */
  @Column(nullable = false, length = 50)
  private String type;

  @Column(name = "reference_id")
  private Long referenceId;
  @Column(name = "reference_type", length = 50)
  private String referenceType;

  @Column(name = "is_read")
  @Builder.Default
  private boolean read = false;
}
