package com.sliit.studentplatform.module1.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for sending a group invitation. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendInvitationRequest {

  @NotNull(message = "Group ID is required")
  private Long groupId;

  @NotNull(message = "Invitee ID is required")
  private Long inviteeId;

  @Size(max = 500)
  private String message;
}
