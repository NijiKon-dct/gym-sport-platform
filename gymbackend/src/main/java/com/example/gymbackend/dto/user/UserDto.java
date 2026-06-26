package com.example.gymbackend.dto.user;

public record UserDto(
        Long id,
        String account,
        String username,
        String email,
        String phone,
        Boolean isStudent,
        String school,
        String studentNumber,
        String nickname,
        String avatar,
        String bio,
        String gender,
        Boolean isAdmin
) {
}

