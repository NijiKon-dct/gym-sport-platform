package com.example.gymbackend.repository;

import com.example.gymbackend.model.VenueFavorite;
import com.example.gymbackend.model.VenueFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VenueFavoriteRepository extends JpaRepository<VenueFavorite, VenueFavoriteId> {

    Optional<VenueFavorite> findByUserIdAndVenueId(Long userId, Long venueId);

    List<VenueFavorite> findByUserId(Long userId);

    List<VenueFavorite> findByVenueId(Long venueId);

    boolean existsByUserIdAndVenueId(Long userId, Long venueId);

    void deleteByUserIdAndVenueId(Long userId, Long venueId);
}















