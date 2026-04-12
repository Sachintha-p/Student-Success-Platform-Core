package com.sliit.studentplatform.module1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEmailInviteRequest {

    @NotBlank(message = "Email or Registration Number is required")
    private String email;

}