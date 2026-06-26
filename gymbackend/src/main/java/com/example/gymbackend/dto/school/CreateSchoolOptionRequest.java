package com.example.gymbackend.dto.school;

import jakarta.validation.constraints.NotBlank;

public record CreateSchoolOptionRequest(
        @NotBlank String name
) {
}

