package com.sliit.studentplatform.module1.dto.request; // Make sure this matches your actual root package!

public class InviteRequestDTO {
    private String email;

    // Default Constructor
    public InviteRequestDTO() {}

    // Getter and Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}