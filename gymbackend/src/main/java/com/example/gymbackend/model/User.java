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
@Table(name = "users")
public class User extends BaseEntity {
    // 确保Builder模式下roles字段也被正确初始化
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name")
    private Set<String> roles = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String account;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    private Boolean isStudent;

    @Column(length = 100)
    private String school;

    @Column(length = 50)
    private String studentNumber;

    @Column(length = 100)
    private String nickname;

    @Column(length = 500)
    private String avatar;

    @Column(length = 500)
    private String bio;

    @Column(length = 10)
    private String gender;

    private Boolean isAdmin;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_topic_subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @Builder.Default
    private Set<Topic> subscribedTopics = new HashSet<>();

    // 上面已经定义了roles字段
}

