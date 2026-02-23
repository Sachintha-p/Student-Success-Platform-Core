package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.common.enums.Priority;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import jakarta.persistence.*;
import lombok.*;

/** A Kanban task card in a project group board. */
@Entity
@Table(name = "kanban_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanTask extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProjectGroup group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignee_id")
  private User assignee;

  @Column(nullable = false, length = 200)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;

  /** Kanban column: BACKLOG | TODO | IN_PROGRESS | REVIEW | DONE */
  @Column(nullable = false, length = 30)
  @Builder.Default
  private String column = "BACKLOG";

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private Priority priority = Priority.MEDIUM;

  @Column(name = "story_points")
  private Integer storyPoints;
}
