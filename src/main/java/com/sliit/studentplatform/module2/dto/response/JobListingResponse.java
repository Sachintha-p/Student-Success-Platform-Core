package com.sliit.studentplatform.module2.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class JobListingResponse {
    private Long id;
    private String title;
    private String company;
    private String description;
    private String[] requiredSkills;
    private String type;
    private String location;
    private boolean remote;
    private LocalDate deadline;
    private boolean active;
    private Long postedById;
}