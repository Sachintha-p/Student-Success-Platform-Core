package com.sliit.studentplatform.module1.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class JoinRequestResponse {
    private Long id;
    private String studentName;
    private String teamName;
    private List<String> studentSkills;
}