package com.sliit.studentplatform.module4.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ConversationSummaryResponse {
    private Long id;
    private Long userId;
    private String studentName;
    private String title;
    private String subject;
    private LocalDateTime createdAt;
}
