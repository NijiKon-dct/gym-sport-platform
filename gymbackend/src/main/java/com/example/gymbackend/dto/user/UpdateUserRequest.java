package com.example.gymbackend.dto.user;

public record UpdateUserRequest(
        String username,
        String email,
        String phone,
        Boolean isStudent,
        String school,
        String studentNumber,
        String nickname,
        String avatar,
        String bio,
        Boolean isAdmin,
        String password
) {
}

