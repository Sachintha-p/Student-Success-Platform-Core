package com.sliit.studentplatform.module3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RSVPRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "GOING|NOT_GOING|MAYBE", message = "Status must be GOING, NOT_GOING, or MAYBE")
    private String status;
}
