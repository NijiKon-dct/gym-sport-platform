package com.example.gymbackend.repository;

import com.example.gymbackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
    
    // 查找顶级评论（没有父评论的评论），并预加载回复
    @Query("SELECT DISTINCT c FROM Comment c LEFT JOIN FETCH c.replies r LEFT JOIN FETCH c.user LEFT JOIN FETCH c.parentComment LEFT JOIN FETCH r.user WHERE c.post.id = :postId AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(@Param("postId") Long postId);
    
    // 查找某个评论的所有回复
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.parentComment WHERE c.parentComment.id = :parentCommentId ORDER BY c.createdAt ASC")
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(@Param("parentCommentId") Long parentCommentId);
    
    // 查找某个动态的所有评论（包括回复），用于批量加载
    @Query("SELECT DISTINCT c FROM Comment c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.parentComment WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
    List<Comment> findAllByPostIdWithUsers(@Param("postId") Long postId);
}

