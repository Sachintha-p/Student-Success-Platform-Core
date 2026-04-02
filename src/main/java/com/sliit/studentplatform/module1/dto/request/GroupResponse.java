package com.sliit.studentplatform.module1.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private int maxMembers;
    private Long leaderId;
}