package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.topic.TopicResponse;
import com.example.gymbackend.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public ApiResponse<List<TopicResponse>> listTopics(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long currentUserId) {
        return ApiResponse.ok(topicService.listTopics(keyword, currentUserId));
    }

    @GetMapping("/subscribed")
    public ApiResponse<List<TopicResponse>> listSubscribedTopics(@RequestParam Long userId) {
        return ApiResponse.ok(topicService.listSubscribedTopics(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<TopicResponse> getTopic(
            @PathVariable Long id,
            @RequestParam(required = false) Long currentUserId) {
        return ApiResponse.ok(topicService.getTopic(id, currentUserId));
    }

    @GetMapping("/name/{name}")
    public ApiResponse<TopicResponse> getTopicByName(
            @PathVariable String name,
            @RequestParam(required = false) Long currentUserId) {
        return ApiResponse.ok(topicService.getTopicByName(name, currentUserId));
    }

    @PostMapping("/{id}/subscribe")
    public ApiResponse<TopicResponse> subscribeTopic(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ApiResponse.ok(topicService.subscribeTopic(id, userId));
    }

    @PostMapping("/{id}/unsubscribe")
    public ApiResponse<TopicResponse> unsubscribeTopic(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ApiResponse.ok(topicService.unsubscribeTopic(id, userId));
    }
}
















