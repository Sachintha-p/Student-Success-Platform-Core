package com.sliit.studentplatform.module1.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InvitationResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long inviterId;
    private String inviterName;
    private String status;
    private String message;
    private LocalDateTime createdAt;
}