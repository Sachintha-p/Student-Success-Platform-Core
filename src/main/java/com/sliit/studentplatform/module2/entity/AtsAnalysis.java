package com.sliit.studentplatform.module2.entity;

import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsAnalysis extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long resumeId;

  private int atsScore;

  @Column(columnDefinition = "TEXT")
  private String weakPoints;

  @Column(columnDefinition = "TEXT")
  private String improvements;
}