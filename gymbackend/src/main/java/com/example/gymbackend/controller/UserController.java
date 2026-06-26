package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.user.UpdateUserRequest;
import com.example.gymbackend.dto.user.UserDto;
import com.example.gymbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserDto>> listUsers() {
        return ApiResponse.ok(userService.listUsers());
    }

    @GetMapping("/search")
    public ApiResponse<List<UserDto>> searchUsers(@RequestParam String keyword) {
        return ApiResponse.ok(userService.searchUsers(keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUser(@PathVariable Long id) {
        return ApiResponse.ok(userService.getUser(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDto> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.ok(null);
    }
}

