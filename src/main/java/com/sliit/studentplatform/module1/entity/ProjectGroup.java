package com.sliit.studentplatform.module1.entity;

import com.sliit.studentplatform.auth.entity.User;
import com.sliit.studentplatform.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student project group (team) in the Smart Team Matchmaker module.
 * Merged to include both Team properties and Service-layer requirements.
 */
@Entity
@Table(name = "project_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectGroup extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

<<<<<<< Updated upstream
    @Column(name = "max_members", nullable = false)
    private int maxMembers;

    // --- YOUR ADDED FIELDS (Required for your GroupService) ---
    @Column(name = "leader_id")
    private Long leaderId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // ----------------------------------------------------------

    // --- TEAM'S EXISTING FIELDS ---
    @Column(name = "required_skills", columnDefinition = "text[]")
    private String[] requiredSkills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members = new ArrayList<>();

    @Column(name = "is_open", nullable = false)
    @Builder.Default
    private boolean open = true;

    @Column(length = 100)
    private String subject;
=======
    /** Maximum number of members allowed in this group. */
    @Column(name = "max_members", nullable = false)
    private int maxMembers;

    /** Required skills as a PostgreSQL text array. */
    @Column(name = "required_skills", columnDefinition = "text[]")
    private String[] requiredSkills;

    /** The user who created and leads this group. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members = new ArrayList<>();

    @Column(name = "is_open", nullable = false)
    @Builder.Default
    private boolean open = true;

    /** Optional module/subject this team is working on. */
    @Column(length = 100)
    private String subject;

    // --- NEW FIELDS: Target Year and Semester ---
    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    @Column(name = "semester")
    private Integer semester;
>>>>>>> Stashed changes
}