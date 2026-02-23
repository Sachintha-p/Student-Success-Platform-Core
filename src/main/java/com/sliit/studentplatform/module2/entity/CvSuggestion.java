package com.sliit.studentplatform.module2.entity;

import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/** AI-generated CV improvement suggestion. */
@Entity
@Table(name = "cv_suggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvSuggestion extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resume_id", nullable = false)
  private Resume resume;

  @Column(nullable = false, length = 100)
  private String section;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String suggestion;

  @Column(nullable = false, length = 20)
  private String priority;

  @Column(name = "applied")
  @Builder.Default
  private boolean applied = false;
}
