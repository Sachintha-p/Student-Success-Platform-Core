package com.sliit.studentplatform.auth.entity;

import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Extended profile for student users.
 *
 * <p>
 * Linked one-to-one with {@link User}. Holds SLIIT-specific data such as
 * the student registration number, degree programme, and skills list.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  /** SLIIT registration number, e.g. IT21234567. */
  @Column(name = "registration_number", nullable = false, unique = true, length = 20)
  private String registrationNumber;

  @Column(name = "degree_programme", length = 100)
  private String degreeProgramme;

  /** Current year of study (1–4). */
  @Column(name = "year_of_study")
  private Integer yearOfStudy;

  /** Current semester (1–8). */
  private Integer semester;

  /** GPA on a 4.0 scale. */
  private Double gpa;

  /**
   * List of self-declared skills, stored as a PostgreSQL text array.
   * Example: {Java, Python, ML, React}
   */
  @Column(columnDefinition = "text[]")
  private String[] skills;

  /** Brief student bio / description. */
  @Column(columnDefinition = "TEXT")
  private String bio;

  /** URL to the student's profile photo. */
  @Column(name = "profile_picture_url")
  private String profilePictureUrl;
}
