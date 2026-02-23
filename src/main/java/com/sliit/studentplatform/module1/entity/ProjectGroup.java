package com.sliit.studentplatform.module1.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student project group (team) in the Smart Team Matchmaker
 * module.
 */
@Entity
@Table(name = "project_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectGroup extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  /** Maximum number of members allowed in this group. */
  @Column(name = "max_members", nullable = false)
  private int maxMembers;

  /** Required skills as a PostgreSQL text array. */
  @Column(name = "required_skills", columnDefinition = "text[]")
  private String[] requiredSkills;

  /** The user who created and leads this group. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Builder.Default
  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupMember> members = new ArrayList<>();

  @Column(name = "is_open", nullable = false)
  @Builder.Default
  private boolean open = true;

  /** Optional module/subject this team is working on. */
  @Column(length = 100)
  private String subject;
}
