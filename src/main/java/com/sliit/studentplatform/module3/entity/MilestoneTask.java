package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.common.enums.Priority;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/** A task within a project milestone. */
@Entity
@Table(name = "milestone_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneTask extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "milestone_id", nullable = false)
  private ProjectMilestone milestone;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignee_id")
  private User assignee;

  @Column(nullable = false, length = 200)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(name = "due_date")
  private LocalDate dueDate;
  @Column(nullable = false, length = 30)
  @Builder.Default
  private String status = "TODO";

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private Priority priority = Priority.MEDIUM;
}
