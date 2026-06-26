package com.example.gymbackend.dto.auth;

import com.example.gymbackend.dto.user.UserDto;

public record AuthResponse(
        String token,
        UserDto user
) {
}

