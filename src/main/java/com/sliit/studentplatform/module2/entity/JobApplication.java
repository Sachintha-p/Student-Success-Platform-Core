package com.sliit.studentplatform.module2.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;

/** A student's application to a specific job listing. */
@Entity
@Table(name = "job_applications", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "job_listing_id", "user_id" }, name = "uq_job_application")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_listing_id", nullable = false)
  private JobListing jobListing;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resume_id")
  private Resume resume;

  @Column(name = "cover_letter", columnDefinition = "TEXT")
  private String coverLetter;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private Status status = Status.PENDING;

  @Column(name = "recruiter_notes", columnDefinition = "TEXT")
  private String recruiterNotes;
}
