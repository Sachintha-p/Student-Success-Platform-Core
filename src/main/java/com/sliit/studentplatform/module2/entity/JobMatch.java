package com.sliit.studentplatform.module2.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/** AI-computed match between a student and a job listing. */
@Entity
@Table(name = "job_matches", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "user_id", "job_listing_id" }, name = "uq_job_match")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatch extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_listing_id", nullable = false)
  private JobListing jobListing;

  @Column(name = "match_score", nullable = false)
  private Double matchScore;

  @Column(name = "matched_skills", columnDefinition = "text[]")
  private String[] matchedSkills;

  @Column(name = "missing_skills", columnDefinition = "text[]")
  private String[] missingSkills;
}
