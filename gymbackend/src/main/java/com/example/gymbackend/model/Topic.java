package com.example.gymbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "topics")
public class Topic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // 话题名称（不含#号）

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Integer postCount = 0; // 动态数量

    @Column(nullable = false)
    @Builder.Default
    private Integer subscriberCount = 0; // 订阅者数量

    @ManyToMany(mappedBy = "topics", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Post> posts = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedTopics", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> subscribers = new HashSet<>();
}
















