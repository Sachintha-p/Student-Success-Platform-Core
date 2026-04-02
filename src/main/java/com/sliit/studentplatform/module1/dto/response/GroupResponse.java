package com.sliit.studentplatform.module1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Response DTO for a project group. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private int maxMembers;
    private int currentMembers;
    private String[] requiredSkills;
    private String subject;
    private boolean open;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;

    // --- NEW FIELDS: Target Year and Semester ---
    private Integer yearOfStudy;
    private Integer semester;
}