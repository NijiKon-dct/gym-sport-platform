package com.example.gymbackend.service;

import com.example.gymbackend.dto.chat.ChatDto;
import com.example.gymbackend.dto.chat.CreateChatRequest;
import com.example.gymbackend.dto.chat.SendMessageRequest;
import com.example.gymbackend.dto.friend.FriendDto;
import com.example.gymbackend.dto.friend.FriendRequestCreateRequest;
import com.example.gymbackend.dto.friend.FriendRequestDto;
import com.example.gymbackend.exception.BadRequestException;
import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.FriendRequest;
import com.example.gymbackend.model.FriendRequestStatus;
import com.example.gymbackend.model.Friendship;
import com.example.gymbackend.model.User;
import com.example.gymbackend.repository.FriendRequestRepository;
import com.example.gymbackend.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserService userService;
    private final ChatService chatService;

    @Transactional(readOnly = true)
    public List<FriendDto> listFriends(Long userId) {
        return friendshipRepository.findByUserId(userId).stream()
                .map(this::toFriendDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestDto> listRequests(Long userId) {
        return friendRequestRepository.findByToUserIdAndStatus(userId, FriendRequestStatus.PENDING)
                .stream().map(this::toRequestDto).toList();
    }

    public FriendRequestDto sendRequest(FriendRequestCreateRequest request) {
        if (request.fromUserId().equals(request.toUserId())) {
            throw new BadRequestException("不能添加自己为好友");
        }
        User from = userService.findById(request.fromUserId());
        User to = userService.findById(request.toUserId());

        // 检查是否已经是好友
        boolean alreadyFriends = friendshipRepository.findByUserIdAndFriendId(from.getId(), to.getId()).isPresent()
                || friendshipRepository.findByUserIdAndFriendId(to.getId(), from.getId()).isPresent();
        if (alreadyFriends) {
            throw new BadRequestException("你们已经是好友了");
        }

        // 检查是否已经存在任何状态的好友请求（处理唯一约束）
        FriendRequest existingRequest = friendRequestRepository.findByFromUserIdAndToUserId(
                from.getId(), to.getId()
        ).orElse(null);

        FriendRequest saved;
        if (existingRequest != null) {
            // 如果已存在请求
            if (existingRequest.getStatus() == FriendRequestStatus.PENDING) {
                // 如果是待处理状态，不允许重复发送
                throw new BadRequestException("已经向该用户发送过好友请求，请等待对方处理");
            } else {
                // 如果是已处理状态（ACCEPTED/REJECTED），更新为新的待处理请求
                existingRequest.setStatus(FriendRequestStatus.PENDING);
                existingRequest.setMessage(request.message());
                saved = friendRequestRepository.save(existingRequest);
            }
        } else {
            // 如果不存在，创建新请求
            FriendRequest friendRequest = FriendRequest.builder()
                    .fromUser(from)
                    .toUser(to)
                    .status(FriendRequestStatus.PENDING)
                    .message(request.message())
                    .build();
            saved = friendRequestRepository.save(friendRequest);
        }

        // 发送一条系统私信给被添加方，提示有新的好友请求（最佳努力，失败不影响主流程）
        try {
            // 创建或获取聊天会话（即使之前删除过好友，聊天会话可能仍然存在）
            ChatDto chat = chatService.createChat(new CreateChatRequest(from.getId(), to.getId()));
            String requesterName = from.getNickname() != null && !from.getNickname().isBlank()
                    ? from.getNickname()
                    : from.getUsername();
            String tips = request.message() != null && !request.message().isBlank()
                    ? "：" + request.message()
                    : "";
            // 发送系统消息，即使聊天会话已存在也能正常发送
            chatService.sendMessage(new SendMessageRequest(
                    chat.id(),
                    from.getId(),
                    to.getId(),
                    "【好友请求】" + requesterName + " 想添加你为好友" + tips,
                    null
            ));
        } catch (Exception e) {
            // 记录错误但不影响好友请求的创建
            // 在实际生产环境中应该使用日志框架记录
            System.err.println("发送好友请求系统消息失败: " + e.getMessage());
            e.printStackTrace();
        }

        return toRequestDto(saved);
    }

    public FriendDto acceptRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("请求不存在"));
        if (!request.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new BadRequestException("该请求已处理");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        createFriendship(request.getFromUser(), request.getToUser());
        createFriendship(request.getToUser(), request.getFromUser());
        
        // 发送系统消息通知请求发起方，好友请求已被接受
        try {
            ChatDto chat = chatService.createChat(new CreateChatRequest(
                    request.getToUser().getId(), request.getFromUser().getId()));
            String accepterName = request.getToUser().getNickname() != null 
                    && !request.getToUser().getNickname().isBlank()
                    ? request.getToUser().getNickname()
                    : request.getToUser().getUsername();
            chatService.sendMessage(new SendMessageRequest(
                    chat.id(),
                    request.getToUser().getId(),
                    request.getFromUser().getId(),
                    "【系统消息】" + accepterName + " 已接受你的好友请求，你们现在是好友了！",
                    null
            ));
        } catch (Exception e) {
            System.err.println("发送接受好友请求通知失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return toFriendDto(friendshipRepository.findByUserIdAndFriendId(
                request.getToUser().getId(), request.getFromUser().getId()
        ).orElseThrow());
    }

    public FriendRequestDto rejectRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("请求不存在"));
        request.setStatus(FriendRequestStatus.REJECTED);
        return toRequestDto(request);
    }

    public void removeFriend(Long userId, Long friendId) {
        friendshipRepository.findByUserIdAndFriendId(userId, friendId)
                .ifPresent(friendshipRepository::delete);
        friendshipRepository.findByUserIdAndFriendId(friendId, userId)
                .ifPresent(friendshipRepository::delete);
    }

    private void createFriendship(User user, User friend) {
        friendshipRepository.findByUserIdAndFriendId(user.getId(), friend.getId())
                .ifPresentOrElse(existing -> {}, () -> {
                    Friendship friendship = Friendship.builder()
                            .user(user)
                            .friend(friend)
                            .isOnline(Boolean.TRUE)
                            .lastSeen(LocalDateTime.now())
                            .build();
                    friendshipRepository.save(friendship);
                });
    }

    private FriendDto toFriendDto(Friendship friendship) {
        User friend = friendship.getFriend();
        return new FriendDto(
                friend.getId(),
                friend.getUsername(),
                friend.getNickname(),
                friend.getAvatar(),
                friend.getSchool(),
                friendship.getIsOnline(),
                friendship.getLastSeen()
        );
    }

    private FriendRequestDto toRequestDto(FriendRequest request) {
        return new FriendRequestDto(
                request.getId(),
                request.getFromUser().getId(),
                request.getFromUser().getUsername(),
                request.getFromUser().getAvatar(),
                request.getMessage(),
                request.getStatus().name().toLowerCase(),
                request.getCreatedAt()
        );
    }
}

