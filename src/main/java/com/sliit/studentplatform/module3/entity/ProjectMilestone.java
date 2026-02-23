package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/** A milestone (deadline) within a project group's timeline. */
@Entity
@Table(name = "project_milestones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMilestone extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProjectGroup group;

  @Column(nullable = false, length = 200)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(name = "due_date")
  private LocalDate dueDate;
  @Column(nullable = false, length = 30)
  @Builder.Default
  private String status = "PENDING";
}
