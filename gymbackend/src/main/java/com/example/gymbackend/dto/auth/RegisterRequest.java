package com.example.gymbackend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String account,
        @NotBlank String username,
        @NotBlank String password,
        @Email String email,
        String phone,
        Boolean isStudent,
        String school,
        String studentNumber,
        String gender
) {
}

