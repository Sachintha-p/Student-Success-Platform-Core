package com.sliit.studentplatform.module1.dto.response;

import com.sliit.studentplatform.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Response DTO for a team invitation. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
  private Long id;
  private Long groupId;
  private String groupName;
  private Long inviterId;
  private String inviterName;
  private Long inviteeId;
  private String inviteeName;
  private Status status;
  private String message;
  private LocalDateTime expiresAt;
  private LocalDateTime createdAt;
}
