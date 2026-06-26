package com.example.gymbackend.service;

import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.User;
import com.example.gymbackend.model.Venue;
import com.example.gymbackend.model.VenueFavorite;
import com.example.gymbackend.repository.UserRepository;
import com.example.gymbackend.repository.VenueFavoriteRepository;
import com.example.gymbackend.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VenueInteractionService {

    private final VenueFavoriteRepository favoriteRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    // ========== 收藏相关 ==========
    
    public void addFavorite(Long userId, Long venueId) {
        if (favoriteRepository.existsByUserIdAndVenueId(userId, venueId)) {
            return; // 已经收藏，直接返回
        }
        User user = getUser(userId);
        Venue venue = getVenue(venueId);
        
        VenueFavorite favorite = VenueFavorite.builder()
                .userId(userId)
                .venueId(venueId)
                .user(user)
                .venue(venue)
                .build();
        favoriteRepository.save(favorite);
    }

    public void removeFavorite(Long userId, Long venueId) {
        favoriteRepository.deleteByUserIdAndVenueId(userId, venueId);
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long venueId) {
        return favoriteRepository.existsByUserIdAndVenueId(userId, venueId);
    }

    @Transactional(readOnly = true)
    public List<VenueFavorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Long> getUserFavoriteVenueIds(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(VenueFavorite::getVenueId)
                .collect(Collectors.toList());
    }

    // ========== 辅助方法 ==========
    
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }

    private Venue getVenue(Long venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("场馆不存在"));
    }
}

