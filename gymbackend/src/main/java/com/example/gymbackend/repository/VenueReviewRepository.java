package com.example.gymbackend.repository;

import com.example.gymbackend.model.VenueReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VenueReviewRepository extends JpaRepository<VenueReview, Long> {

    List<VenueReview> findByVenueIdOrderByCreatedAtDesc(Long venueId);

    Optional<VenueReview> findByVenueIdAndUserId(Long venueId, Long userId);

    @Query("SELECT AVG(r.rating) FROM VenueReview r WHERE r.venue.id = :venueId")
    Double findAverageRatingByVenueId(@Param("venueId") Long venueId);

    @Query("SELECT COUNT(r) FROM VenueReview r WHERE r.venue.id = :venueId")
    Long countByVenueId(@Param("venueId") Long venueId);
}















