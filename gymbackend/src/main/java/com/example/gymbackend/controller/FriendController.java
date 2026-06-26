package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.friend.FriendDto;
import com.example.gymbackend.dto.friend.FriendRequestCreateRequest;
import com.example.gymbackend.dto.friend.FriendRequestDto;
import com.example.gymbackend.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/friends")
    public ApiResponse<List<FriendDto>> listFriends(@RequestParam Long userId) {
        return ApiResponse.ok(friendService.listFriends(userId));
    }

    @DeleteMapping("/friends")
    public ApiResponse<Void> removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        friendService.removeFriend(userId, friendId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/friend-requests")
    public ApiResponse<List<FriendRequestDto>> listRequests(@RequestParam Long userId) {
        return ApiResponse.ok(friendService.listRequests(userId));
    }

    @PostMapping("/friend-requests")
    public ApiResponse<FriendRequestDto> createRequest(@Valid @RequestBody FriendRequestCreateRequest request) {
        return ApiResponse.ok(friendService.sendRequest(request));
    }

    @PostMapping("/friend-requests/{id}/accept")
    public ApiResponse<FriendDto> accept(@PathVariable Long id) {
        return ApiResponse.ok(friendService.acceptRequest(id));
    }

    @PostMapping("/friend-requests/{id}/reject")
    public ApiResponse<FriendRequestDto> reject(@PathVariable Long id) {
        return ApiResponse.ok(friendService.rejectRequest(id));
    }
}

