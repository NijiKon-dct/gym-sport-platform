-- 体育馆管理系统数据库创建脚本
-- 数据库: gymdb
-- 字符集: utf8mb4

-- 创建数据库
CREATE DATABASE IF NOT EXISTS gymdb 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE gymdb;

-- 1. 用户表 (users)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account VARCHAR(50) NOT NULL UNIQUE COMMENT '账号',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    username VARCHAR(100) NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    is_student BOOLEAN COMMENT '是否为学生',
    school VARCHAR(100) COMMENT '学校',
    student_number VARCHAR(50) COMMENT '学生号',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像路径',
    bio VARCHAR(500) COMMENT '个人简介',
    gender VARCHAR(10) COMMENT '性别: MALE, FEMALE, OTHER',
    is_admin BOOLEAN COMMENT '是否为管理员',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_account (account),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 用户角色表 (user_roles)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    PRIMARY KEY (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色表';

-- 3. 场馆表 (venues)
CREATE TABLE IF NOT EXISTS venues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL COMMENT '场馆名称',
    address VARCHAR(255) COMMENT '地址',
    type VARCHAR(100) COMMENT '类型',
    capacity INT COMMENT '容纳人数',
    price DOUBLE COMMENT '价格/小时',
    image VARCHAR(500) COMMENT '场馆图片路径',
    description VARCHAR(1000) COMMENT '描述',
    open_time VARCHAR(10) COMMENT '开放时间',
    close_time VARCHAR(10) COMMENT '关闭时间',
    school VARCHAR(100) COMMENT '所属学校',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_school (school)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场馆表';

-- 4. 预约表 (bookings)
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    venue_id BIGINT NOT NULL COMMENT '场馆ID',
    date DATE NOT NULL COMMENT '预约日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    status VARCHAR(20) NOT NULL COMMENT '状态: PENDING, CONFIRMED, CANCELLED',
    price DOUBLE COMMENT '预约时的价格',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_venue_id (venue_id),
    INDEX idx_date (date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

-- 5. 动态表 (posts)
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content VARCHAR(1000) NOT NULL COMMENT '内容',
    video VARCHAR(500) COMMENT '视频',
    venue_id BIGINT COMMENT '关联场馆ID',
    comment_count INT NOT NULL DEFAULT 0 COMMENT '评论数',
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否置顶',
    is_featured BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否精华',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_venue_id (venue_id),
    INDEX idx_created_at (created_at),
    INDEX idx_is_pinned (is_pinned),
    INDEX idx_is_featured (is_featured)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态表';

-- 6. 动态图片表 (post_images)
CREATE TABLE IF NOT EXISTS post_images (
    post_id BIGINT NOT NULL COMMENT '动态ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片路径',
    PRIMARY KEY (post_id, image_url),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态图片表';

-- 7. 动态点赞表 (post_likes)
CREATE TABLE IF NOT EXISTS post_likes (
    post_id BIGINT NOT NULL COMMENT '动态ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态点赞表';

-- 8. 评论表 (comments)
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL COMMENT '动态ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parent_comment_id BIGINT NULL COMMENT '父评论ID（回复评论时使用）',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_comment_id (parent_comment_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 9. 评论点赞表 (comment_likes)
CREATE TABLE IF NOT EXISTS comment_likes (
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    PRIMARY KEY (comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_comment_id (comment_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';

-- 10. 好友请求表 (friend_requests)
CREATE TABLE IF NOT EXISTS friend_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT NOT NULL COMMENT '发送者ID',
    to_user_id BIGINT NOT NULL COMMENT '接收者ID',
    status VARCHAR(20) NOT NULL COMMENT '状态: PENDING, ACCEPTED, REJECTED',
    message VARCHAR(500) COMMENT '请求消息',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_to_user_id (to_user_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_friend_request (from_user_id, to_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友请求表';

-- 11. 好友关系表 (friendships)
CREATE TABLE IF NOT EXISTS friendships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    friend_id BIGINT NOT NULL COMMENT '好友ID',
    is_online BOOLEAN COMMENT '是否在线',
    last_seen DATETIME COMMENT '最后在线时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_friend_id (friend_id),
    UNIQUE KEY uk_friendship (user_id, friend_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友关系表';

-- 12. 聊天会话表 (chat_sessions)
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_a_id BIGINT NOT NULL COMMENT '用户A ID',
    user_b_id BIGINT NOT NULL COMMENT '用户B ID',
    last_message VARCHAR(1000) COMMENT '最后一条消息',
    last_message_time DATETIME COMMENT '最后消息时间',
    unread_count_for_user_a INT NOT NULL DEFAULT 0 COMMENT '用户A未读数',
    unread_count_for_user_b INT NOT NULL DEFAULT 0 COMMENT '用户B未读数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (user_a_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user_b_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_a_id (user_a_id),
    INDEX idx_user_b_id (user_b_id),
    UNIQUE KEY uk_chat_session (user_a_id, user_b_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- 13. 消息表 (messages)
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_id BIGINT NOT NULL COMMENT '聊天会话ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    type VARCHAR(20) NOT NULL COMMENT '消息类型: TEXT, IMAGE, FILE',
    content VARCHAR(2000) NOT NULL COMMENT '消息内容',
    is_read BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已读',
    read_at DATETIME COMMENT '已读时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (chat_id) REFERENCES chat_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_chat_id (chat_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 14. 话题表 (topics)
CREATE TABLE IF NOT EXISTS topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '话题名称（不含#号）',
    description VARCHAR(500) COMMENT '话题描述',
    post_count INT NOT NULL DEFAULT 0 COMMENT '动态数量',
    subscriber_count INT NOT NULL DEFAULT 0 COMMENT '订阅者数量',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_post_count (post_count),
    INDEX idx_subscriber_count (subscriber_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='话题表';

-- 15. 动态-话题关联表 (post_topics)
CREATE TABLE IF NOT EXISTS post_topics (
    post_id BIGINT NOT NULL COMMENT '动态ID',
    topic_id BIGINT NOT NULL COMMENT '话题ID',
    PRIMARY KEY (post_id, topic_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id),
    INDEX idx_topic_id (topic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态-话题关联表';

-- 16. 用户-话题订阅表 (user_topic_subscriptions)
CREATE TABLE IF NOT EXISTS user_topic_subscriptions (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    topic_id BIGINT NOT NULL COMMENT '话题ID',
    created_at DATETIME NOT NULL COMMENT '订阅时间',
    PRIMARY KEY (user_id, topic_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_topic_id (topic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-话题订阅表';

-- 17. 场馆收藏表 (venue_favorites)
CREATE TABLE IF NOT EXISTS venue_favorites (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    venue_id BIGINT NOT NULL COMMENT '场馆ID',
    created_at DATETIME NOT NULL COMMENT '收藏时间',
    PRIMARY KEY (user_id, venue_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_venue_id (venue_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场馆收藏表';

-- 18. 场馆评论表 (venue_reviews)
CREATE TABLE IF NOT EXISTS venue_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_id BIGINT NOT NULL COMMENT '场馆ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    rating INT NOT NULL COMMENT '评分(1-5)',
    content VARCHAR(1000) COMMENT '评论内容',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_venue_id (venue_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_venue_user_review (venue_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场馆评论表';

-- 19. 场馆评论图片表 (venue_review_images)
CREATE TABLE IF NOT EXISTS venue_review_images (
    review_id BIGINT NOT NULL COMMENT '评论ID',
    image_path VARCHAR(500) NOT NULL COMMENT '图片路径',
    PRIMARY KEY (review_id, image_path),
    FOREIGN KEY (review_id) REFERENCES venue_reviews(id) ON DELETE CASCADE,
    INDEX idx_review_id (review_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场馆评论图片表';

-- 20. 场馆评论视频表 (venue_review_videos)
CREATE TABLE IF NOT EXISTS venue_review_videos (
    review_id BIGINT NOT NULL COMMENT '评论ID',
    video_path VARCHAR(500) NOT NULL COMMENT '视频路径',
    PRIMARY KEY (review_id, video_path),
    FOREIGN KEY (review_id) REFERENCES venue_reviews(id) ON DELETE CASCADE,
    INDEX idx_review_id (review_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场馆评论视频表';

