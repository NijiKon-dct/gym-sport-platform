package com.example.gymbackend.repository;

import com.example.gymbackend.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    @Query("SELECT c FROM ChatSession c WHERE c.userA.id = :userId OR c.userB.id = :userId ORDER BY c.lastMessageTime DESC")
    List<ChatSession> findByParticipant(@Param("userId") Long userId);

    @Query("SELECT c FROM ChatSession c WHERE (c.userA.id = :userA AND c.userB.id = :userB) " +
            "OR (c.userA.id = :userB AND c.userB.id = :userA)")
    Optional<ChatSession> findExistingSession(@Param("userA") Long userA, @Param("userB") Long userB);
}

