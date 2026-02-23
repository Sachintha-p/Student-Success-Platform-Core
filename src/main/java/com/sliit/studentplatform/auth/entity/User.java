package com.sliit.studentplatform.auth.entity;

import com.sliit.studentplatform.common.audit.AuditableEntity;
import com.sliit.studentplatform.common.enums.Role;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an authenticated user account in the platform.
 *
 * <p>
 * Both students and lecturers/admins have a {@code User} record.
 * Students additionally have a linked {@link Student} entity.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email", name = "uq_users_email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "full_name", nullable = false, length = 150)
  private String fullName;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  /** BCrypt-hashed password — never store plain text. */
  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  @Column(nullable = false)
  @Builder.Default
  private boolean enabled = true;

  /** One-to-one relation to student details (null for non-student users). */
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Student student;
}
