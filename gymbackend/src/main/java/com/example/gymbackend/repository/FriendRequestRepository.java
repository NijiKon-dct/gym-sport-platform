package com.example.gymbackend.repository;

import com.example.gymbackend.model.FriendRequest;
import com.example.gymbackend.model.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.toUser.id = :userId AND fr.status = :status")
    List<FriendRequest> findByToUserIdAndStatus(@Param("userId") Long userId, @Param("status") FriendRequestStatus status);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.fromUser.id = :userId")
    List<FriendRequest> findByFromUserId(@Param("userId") Long userId);

    // 检查是否存在待处理的好友请求
    @Query("SELECT fr FROM FriendRequest fr WHERE fr.fromUser.id = :fromUserId AND fr.toUser.id = :toUserId AND fr.status = :status")
    java.util.Optional<FriendRequest> findByFromUserIdAndToUserIdAndStatus(
            @Param("fromUserId") Long fromUserId, 
            @Param("toUserId") Long toUserId, 
            @Param("status") FriendRequestStatus status);

    // 检查是否存在任何状态的好友请求（用于处理唯一约束）
    @Query("SELECT fr FROM FriendRequest fr WHERE fr.fromUser.id = :fromUserId AND fr.toUser.id = :toUserId")
    java.util.Optional<FriendRequest> findByFromUserIdAndToUserId(
            @Param("fromUserId") Long fromUserId, 
            @Param("toUserId") Long toUserId);
}

