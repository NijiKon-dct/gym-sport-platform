package com.example.gymbackend.repository;

import com.example.gymbackend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Post p ORDER BY p.isPinned DESC, p.createdAt DESC")
    List<Post> findAllOrdered();

    @Query("SELECT DISTINCT p FROM Post p JOIN p.topics t WHERE t.id = :topicId ORDER BY p.isPinned DESC, p.createdAt DESC")
    List<Post> findByTopicsId(@Param("topicId") Long topicId);

    // 热门排序：按互动数（点赞+评论）排序，置顶和精华优先
    @Query("SELECT p FROM Post p ORDER BY " +
           "p.isPinned DESC, " +
           "p.isFeatured DESC, " +
           "(SIZE(p.likedUserIds) * 2 + p.commentCount) DESC, " +
           "p.createdAt DESC")
    List<Post> findAllOrderedByHot();

    // 精华排序：只返回精华动态，置顶优先，然后按时间
    @Query("SELECT p FROM Post p WHERE p.isFeatured = TRUE ORDER BY " +
           "p.isPinned DESC, " +
           "p.createdAt DESC")
    List<Post> findAllOrderedByFeatured();
}

