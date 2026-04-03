package com.sliit.studentplatform.module2.dto.response;

import com.sliit.studentplatform.common.enums.Status;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {
    private Long id;
    private Long jobListingId;
    private String jobTitle;
    private String companyName;
    private Long userId;
    private String applicantName;
    private Long resumeId;
    private String resumeFileName;
    private String coverLetter;
    private Status status;
    private String recruiterNotes;
    private LocalDateTime appliedAt;
}