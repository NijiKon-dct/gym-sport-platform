package com.example.gymbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venue_reviews")
public class VenueReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer rating; // 1-5分

    @Column(length = 1000)
    private String content;

    @ElementCollection
    @CollectionTable(name = "venue_review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_path")
    @Builder.Default
    private List<String> imagePaths = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "venue_review_videos", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "video_path")
    @Builder.Default
    private List<String> videoPaths = new ArrayList<>();
}















