package com.sliit.studentplatform.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentProfileRequest {

    private String fullName;
    private String degreeProgramme;
    private Integer yearOfStudy;
    private Integer semester;
    private String bio;
    private String[] skills;
    private String profilePictureUrl;

}