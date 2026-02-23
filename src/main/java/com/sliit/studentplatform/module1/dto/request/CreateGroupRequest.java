package com.sliit.studentplatform.module1.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for creating a new project group. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {

  @NotBlank(message = "Group name is required")
  @Size(min = 3, max = 150)
  private String name;

  @Size(max = 1000)
  private String description;

  @NotNull
  @Min(2)
  @Max(10)
  private Integer maxMembers;

  private String[] requiredSkills;

  @Size(max = 100)
  private String subject;
}
