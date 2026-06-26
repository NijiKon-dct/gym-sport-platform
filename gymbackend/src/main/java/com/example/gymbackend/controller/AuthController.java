package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.auth.AuthResponse;
import com.example.gymbackend.dto.auth.LoginRequest;
import com.example.gymbackend.dto.auth.RegisterRequest;
import com.example.gymbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(userService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(userService.register(request));
    }
    
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody Map<String, Object> request) {
        String account = (String) request.get("account");
        String phone = (String) request.get("phone");
        String newPassword = (String) request.get("newPassword");
        // 验证码暂不验证，直接调用重置密码功能
        userService.resetPassword(account, phone, newPassword);
        return ApiResponse.ok(null);
    }
}

