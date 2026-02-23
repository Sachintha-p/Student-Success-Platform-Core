package com.sliit.studentplatform.module2.entity;

import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/** ATS analysis result for a resume against a specific job listing. */
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resume_id", nullable = false)
  private Resume resume;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_listing_id")
  private JobListing jobListing;

  @Column(name = "ats_score", nullable = false)
  private Double atsScore;

  @Column(name = "keyword_matches", columnDefinition = "text[]")
  private String[] keywordMatches;

  @Column(name = "missing_keywords", columnDefinition = "text[]")
  private String[] missingKeywords;

  @Column(name = "ai_feedback", columnDefinition = "TEXT")
  private String aiFeedback;
}
