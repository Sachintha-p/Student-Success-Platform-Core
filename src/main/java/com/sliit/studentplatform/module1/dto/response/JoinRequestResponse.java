package com.sliit.studentplatform.module1.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class JoinRequestResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long inviterId;
    private String inviterName;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    private List<String> studentSkills;
}
