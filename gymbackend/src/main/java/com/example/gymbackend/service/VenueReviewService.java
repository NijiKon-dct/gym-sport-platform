package com.example.gymbackend.service;

import com.example.gymbackend.dto.venue.VenueReviewRequest;
import com.example.gymbackend.dto.venue.VenueReviewResponse;
import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.User;
import com.example.gymbackend.model.Venue;
import com.example.gymbackend.model.VenueReview;
import com.example.gymbackend.repository.UserRepository;
import com.example.gymbackend.repository.VenueRepository;
import com.example.gymbackend.repository.VenueReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VenueReviewService {

    private final VenueReviewRepository reviewRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<VenueReviewResponse> findByVenueId(Long venueId) {
        List<VenueReview> reviews = reviewRepository.findByVenueIdOrderByCreatedAtDesc(venueId);
        return reviews.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VenueReviewResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    public VenueReviewResponse create(Long venueId, Long userId, VenueReviewRequest request) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("场馆不存在"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 检查用户是否已经评论过该场馆
        Optional<VenueReview> existingReview = reviewRepository.findByVenueIdAndUserId(venueId, userId);
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("您已经评论过该场馆，请更新现有评论");
        }

        VenueReview review = VenueReview.builder()
                .venue(venue)
                .user(user)
                .rating(request.rating())
                .content(request.content())
                .imagePaths(request.imagePaths() != null ? new ArrayList<>(request.imagePaths()) : new ArrayList<>())
                .videoPaths(request.videoPaths() != null ? new ArrayList<>(request.videoPaths()) : new ArrayList<>())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public VenueReviewResponse update(Long id, Long userId, VenueReviewRequest request) {
        VenueReview review = getEntity(id);
        
        // 验证用户权限
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("无权修改此评论");
        }

        review.setRating(request.rating());
        review.setContent(request.content());
        if (request.imagePaths() != null) {
            review.setImagePaths(new ArrayList<>(request.imagePaths()));
        }
        if (request.videoPaths() != null) {
            review.setVideoPaths(new ArrayList<>(request.videoPaths()));
        }

        return toResponse(reviewRepository.save(review));
    }

    public void delete(Long id, Long userId) {
        VenueReview review = getEntity(id);
        
        // 验证用户权限
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("无权删除此评论");
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long venueId) {
        Double avgRating = reviewRepository.findAverageRatingByVenueId(venueId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Transactional(readOnly = true)
    public Long getReviewCount(Long venueId) {
        return reviewRepository.countByVenueId(venueId);
    }

    private VenueReview getEntity(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("评论不存在"));
    }

    private VenueReviewResponse toResponse(VenueReview review) {
        User user = review.getUser();
        Venue venue = review.getVenue();
        return new VenueReviewResponse(
                review.getId(),
                venue.getId(),
                user.getId(),
                user.getUsername(),
                user.getAvatar(),
                review.getRating(),
                review.getContent(),
                review.getImagePaths() != null ? new ArrayList<>(review.getImagePaths()) : new ArrayList<>(),
                review.getVideoPaths() != null ? new ArrayList<>(review.getVideoPaths()) : new ArrayList<>(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}

