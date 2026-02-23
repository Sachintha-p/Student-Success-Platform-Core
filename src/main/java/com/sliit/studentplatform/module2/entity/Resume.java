package com.sliit.studentplatform.module2.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/** Represents a student's uploaded resume/CV. */
@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "file_url", nullable = false)
  private String fileUrl;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "content_type", length = 100)
  private String contentType;

  @Column(columnDefinition = "TEXT")
  private String extractedText;

  @Column(name = "is_primary", nullable = false)
  @Builder.Default
  private boolean primary = false;
}
