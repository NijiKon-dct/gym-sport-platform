package com.example.gymbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 1000)
    private String content;

    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    private String video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ElementCollection
    @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id")
    @Builder.Default
    private Set<Long> likedUserIds = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "post_topics",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @Builder.Default
    private Set<Topic> topics = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPinned = false; // 是否置顶

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false; // 是否精华

    @ElementCollection
    @CollectionTable(name = "post_mentions", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id")
    @Builder.Default
    private Set<Long> mentionedUserIds = new HashSet<>(); // 被@的用户ID列表
}

