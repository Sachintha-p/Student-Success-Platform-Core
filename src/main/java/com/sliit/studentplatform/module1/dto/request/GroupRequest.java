package com.sliit.studentplatform.module1.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
public class GroupRequest {
    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    @Min(value = 2, message = "Minimum members should be 2")
    @Max(value = 10, message = "Maximum members should be 10")
    private int maxMembers;
}