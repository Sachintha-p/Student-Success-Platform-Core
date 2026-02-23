package com.sliit.studentplatform.module3.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.module1.entity.ProjectGroup;
import jakarta.persistence.*;
import lombok.*;

/** File shared in a project group. */
@Entity
@Table(name = "shared_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedFile extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ProjectGroup group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uploaded_by", nullable = false)
  private User uploadedBy;

  @Column(name = "file_name", nullable = false)
  private String fileName;
  @Column(name = "file_url", nullable = false)
  private String fileUrl;
  @Column(name = "file_size")
  private Long fileSize;
  @Column(name = "content_type", length = 100)
  private String contentType;
  @Column(length = 200)
  private String description;
}
