package com.sliit.studentplatform.module3.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RSVPResponse {
    private Long id;
    private Long eventId;
    private Long studentId;
    private String status;
    private LocalDateTime rsvpDate;
}
