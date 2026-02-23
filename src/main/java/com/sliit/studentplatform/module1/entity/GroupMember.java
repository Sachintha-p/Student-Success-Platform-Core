package com.sliit.studentplatform.module1.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Represents a user's membership in a {@link ProjectGroup}. */
@Entity
@Table(name = "group_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "group_id", "user_id" }, name = "uq_group_member")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProjectGroup group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "joined_at", nullable = false)
  @Builder.Default
  private LocalDateTime joinedAt = LocalDateTime.now();

  /** Whether this member is the group leader (usually the creator). */
  @Column(name = "is_leader")
  @Builder.Default
  private boolean leader = false;
}
