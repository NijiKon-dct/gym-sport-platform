package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.social.CommentRequest;
import com.example.gymbackend.dto.social.CommentResponse;
import com.example.gymbackend.dto.social.PostRequest;
import com.example.gymbackend.dto.social.PostResponse;
import com.example.gymbackend.dto.social.PostUpdateRequest;
import com.example.gymbackend.service.SocialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SocialController {

    private final SocialService socialService;

    @GetMapping("/posts")
    public ApiResponse<List<PostResponse>> listPosts(
            @RequestParam(required = false) Long currentUserId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String sortBy) {
        return ApiResponse.ok(socialService.listPosts(currentUserId, topicId, sortBy));
    }

    @PostMapping("/posts")
    public ApiResponse<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        return ApiResponse.ok(socialService.createPost(request));
    }

    @PutMapping("/posts/{id}")
    public ApiResponse<PostResponse> updatePost(@PathVariable Long id,
                                                @Valid @RequestBody PostUpdateRequest request,
                                                @RequestParam Long currentUserId) {
        return ApiResponse.ok(socialService.updatePost(id, request, currentUserId));
    }

    @DeleteMapping("/posts/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id, @RequestParam Long currentUserId) {
        socialService.deletePost(id, currentUserId);
        return ApiResponse.ok(null);
    }

    @PostMapping("/posts/{id}/like")
    public ApiResponse<PostResponse> toggleLike(@PathVariable Long id, @RequestParam Long userId) {
        return ApiResponse.ok(socialService.toggleLike(id, userId));
    }

    @GetMapping("/posts/{id}/comments")
    public ApiResponse<List<CommentResponse>> listComments(@PathVariable Long id,
                                                           @RequestParam(required = false) Long currentUserId) {
        return ApiResponse.ok(socialService.listComments(id, currentUserId));
    }

    @PostMapping("/posts/{id}/comments")
    public ApiResponse<CommentResponse> addComment(@PathVariable Long id,
                                                   @Valid @RequestBody CommentRequest request) {
        return ApiResponse.ok(socialService.addComment(id, request));
    }

    /**
     * 设置/取消置顶（管理员功能）
     */
    @PutMapping("/posts/{id}/pin")
    public ApiResponse<PostResponse> togglePin(@PathVariable Long id,
                                              @RequestParam Long currentUserId,
                                              @RequestParam Boolean pinned) {
        return ApiResponse.ok(socialService.setPinned(id, currentUserId, pinned));
    }

    /**
     * 设置/取消精华（管理员功能）
     */
    @PutMapping("/posts/{id}/feature")
    public ApiResponse<PostResponse> toggleFeature(@PathVariable Long id,
                                                  @RequestParam Long currentUserId,
                                                  @RequestParam Boolean featured) {
        return ApiResponse.ok(socialService.setFeatured(id, currentUserId, featured));
    }
}

