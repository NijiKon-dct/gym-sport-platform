package com.example.gymbackend.service;

import com.example.gymbackend.dto.social.CommentRequest;
import com.example.gymbackend.dto.social.CommentResponse;
import com.example.gymbackend.dto.social.PostRequest;
import com.example.gymbackend.dto.social.PostResponse;
import com.example.gymbackend.dto.social.PostUpdateRequest;
import com.example.gymbackend.exception.BadRequestException;
import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.Comment;
import com.example.gymbackend.model.Post;
import com.example.gymbackend.model.Topic;
import com.example.gymbackend.model.User;
import com.example.gymbackend.model.Venue;
import com.example.gymbackend.repository.CommentRepository;
import com.example.gymbackend.repository.PostRepository;
import com.example.gymbackend.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final VenueRepository venueRepository;
    private final UserService userService;
    private final TopicService topicService;

    @Transactional(readOnly = true)
    public List<PostResponse> listPosts(Long currentUserId, Long topicId, String sortBy) {
        List<Post> posts;
        if (topicId != null) {
            posts = postRepository.findByTopicsId(topicId);
        } else {
            // 根据排序方式选择不同的查询
            if ("hot".equals(sortBy)) {
                posts = postRepository.findAllOrderedByHot();
            } else if ("featured".equals(sortBy)) {
                posts = postRepository.findAllOrderedByFeatured();
            } else {
                // 默认按时间排序
                posts = postRepository.findAllOrdered();
            }
        }
        return posts.stream()
                .map(post -> toPostResponse(post, currentUserId))
                .toList();
    }

    public PostResponse createPost(PostRequest request) {
        User user = userService.findById(request.userId());
        Venue venue = null;
        if (request.venueId() != null) {
            venue = venueRepository.findById(request.venueId())
                    .orElseThrow(() -> new ResourceNotFoundException("场馆不存在"));
        }

        Post post = Post.builder()
                .user(user)
                .content(request.content())
                .video(request.video())
                .venue(venue)
                .build();
        if (request.images() != null) {
            post.getImages().addAll(request.images());
        }

        // 处理话题
        if (request.topics() != null && !request.topics().isEmpty()) {
            Set<Topic> topics = topicService.createOrGetTopics(request.topics());
            post.setTopics(topics);
            topicService.incrementPostCount(topics);
        }

        // 处理@好友
        if (request.mentionedUserIds() != null && !request.mentionedUserIds().isEmpty()) {
            post.getMentionedUserIds().addAll(request.mentionedUserIds());
        }

        return toPostResponse(postRepository.save(post), user.getId());
    }

    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long currentUserId) {
        Post post = getOwnedPost(postId, currentUserId);
        if (request.content() != null) post.setContent(request.content());
        if (request.images() != null) {
            post.getImages().clear();
            post.getImages().addAll(request.images());
        }
        if (request.video() != null) {
            post.setVideo(request.video());
        }
        if (request.venueId() != null) {
            Venue venue = venueRepository.findById(request.venueId())
                    .orElseThrow(() -> new ResourceNotFoundException("场馆不存在"));
            post.setVenue(venue);
        }
        // 处理话题更新
        if (request.topics() != null) {
            // 减少旧话题的计数
            Set<Topic> oldTopics = post.getTopics();
            if (!oldTopics.isEmpty()) {
                topicService.decrementPostCount(oldTopics);
            }
            // 设置新话题
            Set<Topic> newTopics = topicService.createOrGetTopics(request.topics());
            post.setTopics(newTopics);
            // 增加新话题的计数
            if (!newTopics.isEmpty()) {
                topicService.incrementPostCount(newTopics);
            }
        }
        // 处理@好友更新
        if (request.mentionedUserIds() != null) {
            post.getMentionedUserIds().clear();
            post.getMentionedUserIds().addAll(request.mentionedUserIds());
        }
        return toPostResponse(postRepository.save(post), currentUserId);
    }

    public void deletePost(Long postId, Long currentUserId) {
        Post post = getOwnedPost(postId, currentUserId);
        // 减少话题计数
        Set<Topic> topics = post.getTopics();
        if (!topics.isEmpty()) {
            topicService.decrementPostCount(topics);
        }
        postRepository.delete(post);
    }

    public PostResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("动态不存在"));
        if (post.getLikedUserIds().contains(userId)) {
            post.getLikedUserIds().remove(userId);
        } else {
            post.getLikedUserIds().add(userId);
        }
        return toPostResponse(post, userId);
    }

    public CommentResponse addComment(Long postId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("动态不存在"));
        User user = userService.findById(request.userId());

        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父评论不存在"));
            // 确保父评论属于同一个动态
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new BadRequestException("父评论不属于该动态");
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(request.content())
                .parentComment(parentComment)
                .build();

        post.setCommentCount(post.getCommentCount() + 1);
        Comment saved = commentRepository.save(comment);
        return toCommentResponse(saved, user.getId());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(Long postId, Long currentUserId) {
        // 一次性加载所有评论（包括回复），避免N+1查询
        List<Comment> allComments = commentRepository.findAllByPostIdWithUsers(postId);
        
        // 构建评论ID到回复列表的映射
        Map<Long, List<Comment>> repliesMap = new HashMap<>();
        for (Comment comment : allComments) {
            if (comment.getParentComment() != null) {
                Long parentId = comment.getParentComment().getId();
                repliesMap.computeIfAbsent(parentId, k -> new java.util.ArrayList<>()).add(comment);
            }
        }
        
        // 只获取顶级评论
        List<Comment> topLevelComments = allComments.stream()
                .filter(comment -> comment.getParentComment() == null)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
        
        // 构建响应，使用预加载的回复数据
        return topLevelComments.stream()
                .map(comment -> toCommentResponseWithReplies(comment, currentUserId, repliesMap))
                .toList();
    }
    
    private CommentResponse toCommentResponseWithReplies(Comment comment, Long currentUserId, 
                                                          java.util.Map<Long, List<Comment>> repliesMap) {
        boolean liked = currentUserId != null && comment.getLikedUserIds().contains(currentUserId);
        
        // 获取父评论信息
        Long parentCommentId = null;
        String parentCommentUsername = null;
        if (comment.getParentComment() != null) {
            parentCommentId = comment.getParentComment().getId();
            parentCommentUsername = comment.getParentComment().getUser().getUsername();
        }
        
        // 从映射中获取回复列表
        List<Comment> replies = repliesMap.getOrDefault(comment.getId(), new java.util.ArrayList<>());
        List<CommentResponse> replyResponses = replies.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(reply -> toCommentResponseSimple(reply, currentUserId))
                .collect(Collectors.toList());
        
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getAvatar(),
                comment.getContent(),
                comment.getLikedUserIds().size(),
                liked,
                parentCommentId,
                parentCommentUsername,
                replyResponses,
                comment.getCreatedAt()
        );
    }
    
    private CommentResponse toCommentResponseSimple(Comment comment, Long currentUserId) {
        boolean liked = currentUserId != null && comment.getLikedUserIds().contains(currentUserId);
        
        Long parentCommentId = null;
        String parentCommentUsername = null;
        if (comment.getParentComment() != null) {
            parentCommentId = comment.getParentComment().getId();
            parentCommentUsername = comment.getParentComment().getUser().getUsername();
        }
        
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getAvatar(),
                comment.getContent(),
                comment.getLikedUserIds().size(),
                liked,
                parentCommentId,
                parentCommentUsername,
                new ArrayList<>(), // 回复不再递归加载
                comment.getCreatedAt()
        );
    }

    private Post getOwnedPost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("动态不存在"));
        // 允许管理员操作任意用户的动态
        if (!post.getUser().getId().equals(currentUserId)) {
            User currentUser = userService.findById(currentUserId);
            if (currentUser.getIsAdmin() == null || !currentUser.getIsAdmin()) {
                throw new BadRequestException("不能操作其他用户的动态");
            }
        }
        return post;
    }

    private PostResponse toPostResponse(Post post, Long currentUserId) {
        boolean liked = currentUserId != null && post.getLikedUserIds().contains(currentUserId);
        List<String> topicNames = post.getTopics().stream()
                .map(Topic::getName)
                .collect(Collectors.toList());
        
        // 计算热门分数和是否热门
        int likes = post.getLikedUserIds().size();
        int comments = post.getCommentCount();
        int hotScore = calculateHotScore(likes, comments, post.getCreatedAt());
        boolean isHot = isHotPost(likes, comments, post.getCreatedAt());
        
        List<Long> mentionedUserIds = post.getMentionedUserIds().stream()
                .collect(Collectors.toList());
        
        return new PostResponse(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getUser().getAvatar(),
                post.getContent(),
                post.getImages(),
                post.getVideo(),
                post.getVenue() != null ? post.getVenue().getId() : null,
                post.getVenue() != null ? post.getVenue().getName() : null,
                topicNames,
                mentionedUserIds,
                likes,
                comments,
                liked,
                post.getIsPinned() != null ? post.getIsPinned() : false,
                post.getIsFeatured() != null ? post.getIsFeatured() : false,
                isHot,
                hotScore,
                post.getCreatedAt()
        );
    }

    /**
     * 计算热门分数
     * 公式：点赞数 × 2 + 评论数 × 3 - 时间衰减
     * 时间衰减：每24小时减少10分，最低为0
     */
    private int calculateHotScore(int likes, int comments, LocalDateTime createdAt) {
        int baseScore = likes * 2 + comments * 3;
        
        // 计算时间衰减（小时）
        long hoursSinceCreation = java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
        int timeDecay = (int) (hoursSinceCreation / 24) * 10; // 每24小时减少10分
        
        int finalScore = Math.max(0, baseScore - timeDecay);
        return finalScore;
    }

    /**
     * 判断是否为热门动态
     * 条件：
     * 1. 热门分数 >= 20 分，或者
     * 2. 点赞数 >= 10 且 评论数 >= 5，或者
     * 3. 创建时间在24小时内且互动数 >= 15
     */
    private boolean isHotPost(int likes, int comments, LocalDateTime createdAt) {
        int hotScore = calculateHotScore(likes, comments, createdAt);
        
        // 条件1：热门分数达到阈值
        if (hotScore >= 20) {
            return true;
        }
        
        // 条件2：高互动数
        if (likes >= 10 && comments >= 5) {
            return true;
        }
        
        // 条件3：24小时内高互动
        long hoursSinceCreation = java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
        if (hoursSinceCreation <= 24 && (likes + comments) >= 15) {
            return true;
        }
        
        return false;
    }

    private CommentResponse toCommentResponse(Comment comment, Long currentUserId) {
        boolean liked = currentUserId != null && comment.getLikedUserIds().contains(currentUserId);
        
        // 获取父评论信息
        Long parentCommentId = null;
        String parentCommentUsername = null;
        if (comment.getParentComment() != null) {
            parentCommentId = comment.getParentComment().getId();
            parentCommentUsername = comment.getParentComment().getUser().getUsername();
        }
        
        // 获取回复列表
        List<CommentResponse> replies = comment.getReplies().stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(reply -> toCommentResponse(reply, currentUserId))
                .collect(Collectors.toList());
        
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getAvatar(),
                comment.getContent(),
                comment.getLikedUserIds().size(),
                liked,
                parentCommentId,
                parentCommentUsername,
                replies,
                comment.getCreatedAt()
        );
    }

    /**
     * 设置/取消置顶（需要管理员权限）
     */
    public PostResponse setPinned(Long postId, Long currentUserId, Boolean pinned) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("动态不存在"));
        
        // 检查是否为管理员
        User user = userService.findById(currentUserId);
        if (user.getIsAdmin() == null || !user.getIsAdmin()) {
            throw new BadRequestException("只有管理员可以设置置顶");
        }
        
        post.setIsPinned(pinned != null && pinned);
        Post saved = postRepository.save(post);
        return toPostResponse(saved, currentUserId);
    }

    /**
     * 设置/取消精华（需要管理员权限）
     */
    public PostResponse setFeatured(Long postId, Long currentUserId, Boolean featured) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("动态不存在"));
        
        // 检查是否为管理员
        User user = userService.findById(currentUserId);
        if (user.getIsAdmin() == null || !user.getIsAdmin()) {
            throw new BadRequestException("只有管理员可以设置精华");
        }
        
        post.setIsFeatured(featured != null && featured);
        Post saved = postRepository.save(post);
        return toPostResponse(saved, currentUserId);
    }
}

