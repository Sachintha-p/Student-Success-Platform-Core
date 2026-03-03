package com.sliit.studentplatform.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailLoginRequest {
    @NotBlank
    @Email
    private String email;
}
