package com.sliit.studentplatform.module1.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Invitation sent from a group to a prospective member. */
@Entity
@Table(name = "team_invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamInvitation extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProjectGroup group;

  /** The user who sent the invitation (group leader). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inviter_id", nullable = false)
  private User inviter;

  /** The user who was invited. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invitee_id", nullable = false)
  private User invitee;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private Status status = Status.PENDING;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(columnDefinition = "TEXT")
  private String message;
}
