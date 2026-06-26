package com.example.gymbackend.repository;

import com.example.gymbackend.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByUserId(Long userId);

    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);
}

