package com.sliit.studentplatform.module1.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;

/** A student's request to join an existing project group. */
@Entity
@Table(name = "join_requests", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "group_id", "requester_id" }, name = "uq_join_request")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequest extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProjectGroup group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requester_id", nullable = false)
  private User requester;

  @Column(columnDefinition = "TEXT")
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private Status status = Status.PENDING;
}
