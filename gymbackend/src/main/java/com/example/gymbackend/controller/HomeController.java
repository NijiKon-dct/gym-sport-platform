package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ApiResponse<String> home() {
        return ApiResponse.ok("体育馆管理系统后端服务正在运行");
    }
    
    @GetMapping("/api")
    public ApiResponse<String> apiHome() {
        return ApiResponse.ok("体育馆管理系统API服务正在运行");
    }
}
