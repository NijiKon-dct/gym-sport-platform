package com.example.gymbackend.repository;

import com.example.gymbackend.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByName(String name);

    @Query("SELECT t FROM Topic t WHERE t.name LIKE %:keyword% ORDER BY t.postCount DESC, t.subscriberCount DESC")
    List<Topic> searchByName(@Param("keyword") String keyword);

    @Query("SELECT t FROM Topic t ORDER BY t.postCount DESC, t.subscriberCount DESC")
    List<Topic> findAllOrderedByPopularity();

    @Query("SELECT t FROM Topic t ORDER BY t.createdAt DESC")
    List<Topic> findAllOrderedByTime();

    @Query("SELECT t FROM Topic t JOIN t.subscribers s WHERE s.id = :userId ORDER BY t.name")
    List<Topic> findBySubscriberId(@Param("userId") Long userId);
}
















