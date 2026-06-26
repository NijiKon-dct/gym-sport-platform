package com.example.gymbackend.service;

import com.example.gymbackend.dto.chat.ChatDto;
import com.example.gymbackend.dto.chat.ChatMessageDto;
import com.example.gymbackend.dto.chat.CreateChatRequest;
import com.example.gymbackend.dto.chat.MarkReadRequest;
import com.example.gymbackend.dto.chat.SendMessageRequest;
import com.example.gymbackend.dto.chat.ReadReceiptDto;
import com.example.gymbackend.exception.BadRequestException;
import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.ChatSession;
import com.example.gymbackend.model.Message;
import com.example.gymbackend.model.MessageType;
import com.example.gymbackend.model.User;
import com.example.gymbackend.repository.ChatSessionRepository;
import com.example.gymbackend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public List<ChatDto> listChats(Long userId) {
        return chatSessionRepository.findByParticipant(userId).stream()
                .map(session -> toChatDto(session, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> listMessages(Long chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId).stream()
                .map(this::toMessageDto)
                .toList();
    }

    public ChatDto createChat(CreateChatRequest request) {
        User current = userService.findById(request.currentUserId());
        User target = userService.findById(request.targetUserId());

        ChatSession session = chatSessionRepository.findExistingSession(current.getId(), target.getId())
                .orElseGet(() -> chatSessionRepository.save(ChatSession.builder()
                        .userA(current)
                        .userB(target)
                        .lastMessage("开始聊天吧～")
                        .lastMessageTime(LocalDateTime.now())
                        .build()));
        return toChatDto(session, current.getId());
    }

    public ChatMessageDto sendMessage(SendMessageRequest request) {
        ChatSession session = chatSessionRepository.findById(request.chatId())
                .orElseThrow(() -> new ResourceNotFoundException("聊天不存在"));
        User sender = userService.findById(request.senderId());
        User receiver = userService.findById(request.receiverId());

        ensureParticipant(session, sender.getId());
        ensureParticipant(session, receiver.getId());

        MessageType msgType = request.type() != null ? request.type() : MessageType.TEXT;
        Message message = Message.builder()
                .chat(session)
                .sender(sender)
                .receiver(receiver)
                .content(request.content())
                .type(msgType)
                .build();
        Message saved = messageRepository.save(message);

        // 聊天列表展示最后一条消息，分享卡片使用简短提示语
        String lastMessagePreview = switch (msgType) {
            case IMAGE -> "[图片]";
            case VIDEO -> "[视频]";
            case EMOJI -> request.content();
            case SHARE_POST -> "分享了一条动态";
            default -> request.content();
        };
        session.setLastMessage(lastMessagePreview);
        session.setLastMessageTime(saved.getCreatedAt());
        if (session.getUserA().getId().equals(receiver.getId())) {
            session.setUnreadCountForUserA(session.getUnreadCountForUserA() + 1);
        } else {
            session.setUnreadCountForUserB(session.getUnreadCountForUserB() + 1);
        }

        ChatMessageDto dto = toMessageDto(saved);

        // 通过 WebSocket 推送给接收方和发送方（实时私聊）
        // 这里不再依赖 STOMP UserDestination，而是使用基于 userId 的 topic，
        // 前端按当前登录用户订阅：/topic/user.{userId}.messages
        try {
            // 推送给接收方
            messagingTemplate.convertAndSend(
                    "/topic/user." + dto.receiverId() + ".messages",
                    dto
            );
            // 也推给发送方，用于本端状态同步
            messagingTemplate.convertAndSend(
                    "/topic/user." + dto.senderId() + ".messages",
                    dto
            );
        } catch (Exception e) {
            // 推送失败不影响主流程
        }

        return dto;
    }

    public ChatDto markAsRead(MarkReadRequest request) {
        ChatSession session = chatSessionRepository.findById(request.chatId())
                .orElseThrow(() -> new ResourceNotFoundException("聊天不存在"));
        ensureParticipant(session, request.userId());

        messageRepository.findByChatIdOrderByCreatedAtAsc(request.chatId()).stream()
                .filter(message -> message.getReceiver().getId().equals(request.userId()))
                .forEach(message -> {
                    message.setIsRead(true);
                    message.setReadAt(LocalDateTime.now());
                });

        if (session.getUserA().getId().equals(request.userId())) {
            session.setUnreadCountForUserA(0);
        } else {
            session.setUnreadCountForUserB(0);
        }
        ChatDto dto = toChatDto(session, request.userId());

        // 向对方推送已读回执，便于实时更新“已读”状态
        Long otherUserId = session.getUserA().getId().equals(request.userId())
                ? session.getUserB().getId()
                : session.getUserA().getId();
        try {
            messagingTemplate.convertAndSend(
                    "/topic/user." + otherUserId + ".read-receipts",
                    new ReadReceiptDto(
                            request.chatId(),
                            request.userId(),
                            LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            // 推送失败不影响主流程
        }

        return dto;
    }

    private void ensureParticipant(ChatSession session, Long userId) {
        if (!session.getUserA().getId().equals(userId) && !session.getUserB().getId().equals(userId)) {
            throw new BadRequestException("当前用户不在该聊天中");
        }
    }

    private ChatDto toChatDto(ChatSession session, Long currentUserId) {
        boolean isUserA = session.getUserA().getId().equals(currentUserId);
        User target = isUserA ? session.getUserB() : session.getUserA();
        int unread = isUserA ? session.getUnreadCountForUserA() : session.getUnreadCountForUserB();
        return new ChatDto(
                session.getId(),
                target.getId(),
                target.getUsername(),
                target.getAvatar(),
                session.getLastMessage(),
                session.getLastMessageTime(),
                unread,
                true
        );
    }

    private ChatMessageDto toMessageDto(Message message) {
        return new ChatMessageDto(
                message.getId(),
                message.getChat().getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getType().name().toLowerCase(),
                message.getIsRead(),
                message.getCreatedAt()
        );
    }
}

