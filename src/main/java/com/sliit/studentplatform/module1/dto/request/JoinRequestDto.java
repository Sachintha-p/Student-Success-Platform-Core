package com.sliit.studentplatform.module1.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for submitting a join request to a group. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestDto {

  @NotNull(message = "Group ID is required")
  private Long groupId;

  @Size(max = 500)
  private String message;
}
