package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.chat.ChatDto;
import com.example.gymbackend.dto.chat.ChatMessageDto;
import com.example.gymbackend.dto.chat.CreateChatRequest;
import com.example.gymbackend.dto.chat.MarkReadRequest;
import com.example.gymbackend.dto.chat.SendMessageRequest;
import com.example.gymbackend.exception.BadRequestException;
import com.example.gymbackend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ApiResponse<List<ChatDto>> listChats(@RequestParam Long userId) {
        return ApiResponse.ok(chatService.listChats(userId));
    }

    @PostMapping
    public ApiResponse<ChatDto> createChat(@Valid @RequestBody CreateChatRequest request) {
        return ApiResponse.ok(chatService.createChat(request));
    }

    @GetMapping("/{chatId}/messages")
    public ApiResponse<List<ChatMessageDto>> listMessages(@PathVariable Long chatId) {
        return ApiResponse.ok(chatService.listMessages(chatId));
    }

    @PostMapping("/{chatId}/messages")
    public ApiResponse<ChatMessageDto> sendMessage(@PathVariable Long chatId,
                                                   @Valid @RequestBody SendMessageRequest request) {
        if (!chatId.equals(request.chatId())) {
            throw new BadRequestException("chatId不匹配");
        }
        return ApiResponse.ok(chatService.sendMessage(request));
    }

    @PostMapping("/{chatId}/read")
    public ApiResponse<ChatDto> markRead(@PathVariable Long chatId,
                                         @Valid @RequestBody MarkReadRequest request) {
        if (!chatId.equals(request.chatId())) {
            throw new BadRequestException("chatId不匹配");
        }
        return ApiResponse.ok(chatService.markAsRead(request));
    }
}

