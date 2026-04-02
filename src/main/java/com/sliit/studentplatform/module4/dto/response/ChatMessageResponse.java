package com.sliit.studentplatform.module4.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long id;
    private String message;
    private String role;
    private LocalDateTime createdAt;

}