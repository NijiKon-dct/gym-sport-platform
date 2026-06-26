

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for booking_reminder_channels
-- ----------------------------
DROP TABLE IF EXISTS `booking_reminder_channels`;
CREATE TABLE `booking_reminder_channels`  (
  `booking_id` bigint NOT NULL,
  `channel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  INDEX `FK67c5bse9c1iibmo8vo6oc99dv`(`booking_id` ASC) USING BTREE,
  CONSTRAINT `FK67c5bse9c1iibmo8vo6oc99dv` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of booking_reminder_channels
-- ----------------------------

-- ----------------------------
-- Table structure for bookings
-- ----------------------------
DROP TABLE IF EXISTS `bookings`;
CREATE TABLE `bookings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `venue_id` bigint NOT NULL COMMENT '场馆ID',
  `date` date NOT NULL COMMENT '预约日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态: PENDING, CONFIRMED, CANCELLED',
  `price` double NULL DEFAULT NULL COMMENT '预约时的价格',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `paid` tinyint(1) NOT NULL DEFAULT 0,
  `waitlist_position` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `idx_date`(`date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bookings
-- ----------------------------
INSERT INTO `bookings` VALUES (1, 2, 1, '2025-12-15', '09:00:00', '11:00:00', 'CANCELLED', 50, NULL, '2025-12-15 17:06:48', '2025-12-16 15:15:19', 0, NULL);
INSERT INTO `bookings` VALUES (5, 2, 1, '2025-12-15', '14:57:00', '19:57:00', 'CANCELLED', 50, '', '2025-12-15 18:57:26', '2025-12-16 10:31:19', 0, NULL);
INSERT INTO `bookings` VALUES (6, 2, 1, '2025-12-16', '14:15:00', '18:15:00', 'CANCELLED', 50, '', '2025-12-16 15:15:47', '2025-12-16 17:06:08', 0, NULL);
INSERT INTO `bookings` VALUES (11, 2, 1, '2025-12-17', '06:00:00', '08:00:00', 'CANCELLED', 50, '', '2025-12-17 16:14:37', '2025-12-19 15:30:35', 1, NULL);
INSERT INTO `bookings` VALUES (13, 2, 1, '2025-12-19', '10:00:00', '11:00:00', 'CANCELLED', 50, '', '2025-12-19 15:30:49', '2025-12-23 14:05:45', 1, NULL);
INSERT INTO `bookings` VALUES (14, 2, 1, '2025-12-23', '12:00:00', '13:30:00', 'CANCELLED', 50, '', '2025-12-23 14:05:58', '2025-12-23 14:18:10', 1, NULL);
INSERT INTO `bookings` VALUES (15, 2, 1, '2025-12-23', '10:00:00', '12:00:00', 'CONFIRMED', 50, '', '2025-12-23 14:18:21', '2025-12-23 14:18:33', 1, NULL);
INSERT INTO `bookings` VALUES (17, 2, 1, '2026-03-20', '20:00:00', '22:00:00', 'CANCELLED', 50, '', '2026-03-08 13:02:46', '2026-03-08 13:03:11', 1, NULL);

-- ----------------------------
-- Table structure for chat_sessions
-- ----------------------------
DROP TABLE IF EXISTS `chat_sessions`;
CREATE TABLE `chat_sessions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_a_id` bigint NOT NULL COMMENT '用户A ID',
  `user_b_id` bigint NOT NULL COMMENT '用户B ID',
  `last_message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后一条消息',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `unread_count_for_user_a` int NOT NULL DEFAULT 0 COMMENT '用户A未读数',
  `unread_count_for_user_b` int NOT NULL DEFAULT 0 COMMENT '用户B未读数',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `unread_count_for_usera` int NOT NULL,
  `unread_count_for_userb` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_chat_session`(`user_a_id` ASC, `user_b_id` ASC) USING BTREE,
  INDEX `idx_user_a_id`(`user_a_id` ASC) USING BTREE,
  INDEX `idx_user_b_id`(`user_b_id` ASC) USING BTREE,
  CONSTRAINT `chat_sessions_ibfk_1` FOREIGN KEY (`user_a_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chat_sessions_ibfk_2` FOREIGN KEY (`user_b_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '聊天会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_sessions
-- ----------------------------
INSERT INTO `chat_sessions` VALUES (1, 2, 3, 'asd', '2026-02-25 18:44:18', 0, 0, '2025-12-15 17:06:48', '2026-02-25 18:44:19', 0, 0);
INSERT INTO `chat_sessions` VALUES (2, 2, 4, '[图片]', '2026-02-25 18:23:27', 0, 0, '2025-12-15 18:45:19', '2026-02-25 18:23:27', 0, 2);
INSERT INTO `chat_sessions` VALUES (3, 1, 2, '【举报处理通知】您的内容（类型：post，ID：3）举报已成立，处理：删除/警告。备注：123123', '2025-12-24 17:54:27', 0, 0, '2025-12-24 17:44:24', '2025-12-24 17:54:50', 0, 0);
INSERT INTO `chat_sessions` VALUES (4, 4, 3, '👏', '2025-12-29 16:20:31', 0, 0, '2025-12-29 16:12:52', '2025-12-29 16:20:31', 0, 0);

-- ----------------------------
-- Table structure for comment_likes
-- ----------------------------
DROP TABLE IF EXISTS `comment_likes`;
CREATE TABLE `comment_likes`  (
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`comment_id`, `user_id`) USING BTREE,
  INDEX `idx_comment_id`(`comment_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `comment_likes_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comment_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment_likes
-- ----------------------------

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_comment_id` bigint NULL DEFAULT NULL COMMENT '父评论ID（回复评论时使用）',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_parent_comment_id`(`parent_comment_id` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 1, 4, NULL, '下次带上我！', '2025-12-15 17:06:48', '2025-12-15 17:06:48');
INSERT INTO `comments` VALUES (4, 8, 2, NULL, '12123', '2025-12-26 17:24:04', '2025-12-26 17:24:04');
INSERT INTO `comments` VALUES (5, 8, 2, NULL, '123123', '2025-12-26 17:24:05', '2025-12-26 17:24:05');
INSERT INTO `comments` VALUES (6, 8, 2, NULL, '3123213', '2025-12-26 17:24:07', '2025-12-26 17:24:07');
INSERT INTO `comments` VALUES (7, 8, 2, 6, '21312', '2025-12-26 17:29:57', '2025-12-26 17:29:57');

-- ----------------------------
-- Table structure for friend_requests
-- ----------------------------
DROP TABLE IF EXISTS `friend_requests`;
CREATE TABLE `friend_requests`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_user_id` bigint NOT NULL COMMENT '发送者ID',
  `to_user_id` bigint NOT NULL COMMENT '接收者ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态: PENDING, ACCEPTED, REJECTED',
  `message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求消息',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_friend_request`(`from_user_id` ASC, `to_user_id` ASC) USING BTREE,
  INDEX `idx_from_user_id`(`from_user_id` ASC) USING BTREE,
  INDEX `idx_to_user_id`(`to_user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `friend_requests_ibfk_1` FOREIGN KEY (`from_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `friend_requests_ibfk_2` FOREIGN KEY (`to_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '好友请求表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of friend_requests
-- ----------------------------
INSERT INTO `friend_requests` VALUES (1, 3, 4, 'ACCEPTED', '一起运动吗？', '2025-12-15 17:06:48', '2025-12-29 16:12:52');
INSERT INTO `friend_requests` VALUES (2, 2, 3, 'ACCEPTED', '你好，我想添加你为好友', '2025-12-17 16:29:18', '2025-12-19 14:10:55');
INSERT INTO `friend_requests` VALUES (9, 2, 4, 'REJECTED', '你好，我想添加你为好友', '2025-12-18 18:46:47', '2025-12-18 18:47:18');

-- ----------------------------
-- Table structure for friendships
-- ----------------------------
DROP TABLE IF EXISTS `friendships`;
CREATE TABLE `friendships`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `friend_id` bigint NOT NULL COMMENT '好友ID',
  `is_online` tinyint(1) NULL DEFAULT NULL COMMENT '是否在线',
  `last_seen` datetime NULL DEFAULT NULL COMMENT '最后在线时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_friendship`(`user_id` ASC, `friend_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_friend_id`(`friend_id` ASC) USING BTREE,
  CONSTRAINT `friendships_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `friendships_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '好友关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of friendships
-- ----------------------------
INSERT INTO `friendships` VALUES (19, 2, 4, 1, '2025-12-19 13:36:57', '2025-12-19 14:06:57', '2025-12-19 14:06:57');
INSERT INTO `friendships` VALUES (20, 4, 2, 0, '2025-12-19 12:06:57', '2025-12-19 14:06:57', '2025-12-19 14:06:57');
INSERT INTO `friendships` VALUES (21, 2, 3, 1, '2025-12-19 14:10:55', '2025-12-19 14:10:55', '2025-12-19 14:10:55');
INSERT INTO `friendships` VALUES (22, 3, 2, 1, '2025-12-19 14:10:55', '2025-12-19 14:10:55', '2025-12-19 14:10:55');
INSERT INTO `friendships` VALUES (23, 3, 4, 1, '2025-12-29 16:12:52', '2025-12-29 16:12:52', '2025-12-29 16:12:52');
INSERT INTO `friendships` VALUES (24, 4, 3, 1, '2025-12-29 16:12:52', '2025-12-29 16:12:52', '2025-12-29 16:12:52');

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chat_id` bigint NOT NULL COMMENT '聊天会话ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型: TEXT, IMAGE, FILE',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `read_at` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_recalled` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_chat_id`(`chat_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`chat_id`) REFERENCES `chat_sessions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_3` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (1, 1, 3, 2, 'TEXT', '明天一起打羽毛球吧！', 1, '2026-03-25 20:11:55', '2025-12-15 17:06:48', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (2, 1, 2, 3, 'TEXT', '好的，明天见！', 1, '2026-02-25 18:44:12', '2025-12-15 17:06:48', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (3, 1, 2, 3, 'TEXT', '【好友请求】运动达人 想添加你为好友：你好，我想添加你为好友', 1, '2026-02-25 18:44:12', '2025-12-19 14:08:43', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (4, 1, 3, 2, 'TEXT', '【系统消息】羽毛球爱好者 已接受你的好友请求，你们现在是好友了！', 1, '2026-03-25 20:11:55', '2025-12-19 14:10:55', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (5, 1, 3, 2, 'TEXT', '1212', 1, '2026-03-25 20:11:55', '2025-12-19 14:13:36', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (6, 1, 3, 2, 'TEXT', '213213', 1, '2026-03-25 20:11:55', '2025-12-19 14:13:39', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (7, 1, 3, 2, 'TEXT', '4214124', 1, '2026-03-25 20:11:55', '2025-12-19 14:13:41', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (8, 1, 2, 3, 'TEXT', '1111', 1, '2026-02-25 18:44:12', '2025-12-19 14:31:14', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (9, 1, 2, 3, 'TEXT', '😀', 1, '2026-02-25 18:44:12', '2025-12-19 14:43:07', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (10, 1, 2, 3, 'TEXT', '😃', 1, '2026-02-25 18:44:12', '2025-12-19 14:43:10', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (11, 1, 2, 3, 'TEXT', '🤜', 1, '2026-02-25 18:44:12', '2025-12-19 14:49:34', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (12, 1, 3, 2, 'TEXT', '12323', 1, '2026-03-25 20:11:55', '2025-12-19 15:44:32', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (13, 1, 2, 3, 'TEXT', '123123213', 1, '2026-02-25 18:44:12', '2025-12-19 16:19:11', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (14, 1, 3, 2, 'TEXT', '123123', 1, '2026-03-25 20:11:55', '2025-12-19 17:57:35', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (15, 1, 3, 2, 'TEXT', '3213123', 1, '2026-03-25 20:11:55', '2025-12-19 17:57:46', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (16, 1, 2, 3, 'TEXT', '23432', 1, '2026-02-25 18:44:12', '2025-12-23 14:21:22', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (17, 1, 2, 3, 'TEXT', '23423', 1, '2026-02-25 18:44:12', '2025-12-23 14:26:12', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (18, 1, 2, 3, 'TEXT', '1211111', 1, '2026-02-25 18:44:12', '2025-12-23 14:26:20', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (19, 1, 2, 3, 'TEXT', '344', 1, '2026-02-25 18:44:12', '2025-12-23 14:26:40', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (20, 1, 3, 2, 'TEXT', '23123', 1, '2026-03-25 20:11:55', '2025-12-23 14:28:11', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (21, 1, 2, 3, 'TEXT', '34423', 1, '2026-02-25 18:44:12', '2025-12-23 14:29:43', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (22, 1, 2, 3, 'TEXT', '324234', 1, '2026-02-25 18:44:12', '2025-12-23 14:29:54', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (23, 1, 2, 3, 'TEXT', '1111', 1, '2026-02-25 18:44:12', '2025-12-23 14:34:36', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (24, 1, 3, 2, 'TEXT', 'wqewqe', 1, '2026-03-25 20:11:55', '2025-12-23 14:34:50', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (25, 2, 2, 4, 'TEXT', 'dsad', 1, '2025-12-29 16:12:55', '2025-12-23 14:35:30', '2025-12-29 16:12:55', b'0');
INSERT INTO `messages` VALUES (26, 1, 3, 2, 'TEXT', '323', 1, '2026-03-25 20:11:55', '2025-12-23 14:41:27', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (27, 1, 2, 3, 'TEXT', '4242', 1, '2026-02-25 18:44:12', '2025-12-23 15:08:12', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (28, 1, 2, 3, 'TEXT', '1111', 1, '2026-02-25 18:44:12', '2025-12-23 15:43:57', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (29, 1, 2, 3, 'TEXT', '1112', 1, '2026-02-25 18:44:12', '2025-12-23 15:44:05', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (30, 1, 3, 2, 'TEXT', '😊', 1, '2026-03-25 20:11:55', '2025-12-23 15:44:12', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (31, 1, 2, 3, 'TEXT', '212', 1, '2026-02-25 18:44:12', '2025-12-23 15:50:09', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (32, 1, 2, 3, 'TEXT', '2323', 1, '2026-02-25 18:44:12', '2025-12-23 15:50:15', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (33, 1, 3, 2, 'TEXT', '3333', 1, '2026-03-25 20:11:55', '2025-12-23 15:59:09', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (34, 1, 3, 2, 'TEXT', '3233', 1, '2026-03-25 20:11:55', '2025-12-23 15:59:14', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (35, 1, 3, 2, 'TEXT', '123123', 1, '2026-03-25 20:11:55', '2025-12-23 15:59:19', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (36, 1, 3, 2, 'TEXT', '3432', 1, '2026-03-25 20:11:55', '2025-12-23 16:10:55', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (37, 1, 3, 2, 'TEXT', '2323', 1, '2026-03-25 20:11:55', '2025-12-23 16:11:44', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (38, 1, 3, 2, 'TEXT', '23', 1, '2026-03-25 20:11:55', '2025-12-23 16:12:00', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (39, 1, 3, 2, 'TEXT', '23', 1, '2026-03-25 20:11:55', '2025-12-23 16:12:03', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (40, 1, 3, 2, 'TEXT', '12', 1, '2026-03-25 20:11:55', '2025-12-23 16:12:12', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (41, 1, 3, 2, 'TEXT', '213', 1, '2026-03-25 20:11:55', '2025-12-23 16:12:15', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (42, 1, 3, 2, 'TEXT', '213213', 1, '2026-03-25 20:11:55', '2025-12-23 16:12:17', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (43, 1, 3, 2, 'TEXT', '42141', 1, '2026-03-25 20:11:55', '2025-12-23 16:12:18', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (44, 1, 3, 2, 'TEXT', '2', 1, '2026-03-25 20:11:55', '2025-12-23 16:15:54', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (45, 1, 2, 3, 'TEXT', '3123', 1, '2026-02-25 18:44:12', '2025-12-23 16:28:49', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (46, 1, 2, 3, 'TEXT', '453', 1, '2026-02-25 18:44:12', '2025-12-23 16:31:34', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (47, 1, 2, 3, 'TEXT', '213', 1, '2026-02-25 18:44:12', '2025-12-23 16:33:29', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (48, 1, 3, 2, 'TEXT', '23123', 1, '2026-03-25 20:11:55', '2025-12-23 16:33:42', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (49, 1, 3, 2, 'TEXT', '213213', 1, '2026-03-25 20:11:55', '2025-12-23 16:33:45', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (50, 1, 3, 2, 'TEXT', '3232', 1, '2026-03-25 20:11:55', '2025-12-23 16:33:46', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (51, 1, 3, 2, 'IMAGE', 'APicture/b77f4c3e-a58f-42ff-9908-df321c4d4619.png', 1, '2026-03-25 20:11:55', '2025-12-23 17:02:17', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (52, 1, 2, 3, 'IMAGE', 'Chat Pictures/63975f07-3c38-4399-b548-5933e00f01c6.png', 1, '2026-02-25 18:44:12', '2025-12-23 17:07:03', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (53, 1, 2, 3, 'VIDEO', 'Chat Video/6479542e-c9bf-4e8d-80c7-f5f5e5f9862c.mp4', 1, '2026-02-25 18:44:12', '2025-12-23 17:38:06', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (54, 1, 2, 3, 'TEXT', '21212', 1, '2026-02-25 18:44:12', '2025-12-23 17:38:55', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (55, 1, 3, 2, 'TEXT', '21321', 1, '2026-03-25 20:11:55', '2025-12-23 17:39:22', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (56, 1, 3, 2, 'TEXT', '【动态分享】Ⅳ 分享了一条动态给你：good\n 查看链接：http://localhost:5173/social?postId=3', 1, '2026-03-25 20:11:55', '2025-12-23 18:27:03', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (57, 1, 3, 2, 'SHARE_POST', '{\"type\":\"share_post\",\"postId\":4,\"author\":\"叶顺\",\"authorAvatar\":\"Profile Picture/33f18b56-fa81-4159-8e92-656c2a45b895.jpg\",\"createdAt\":\"2025-12-18T18:12:45\",\"content\":\"23123231231\",\"image\":\"APicture/6e5a4285-9f65-423a-9c6c-f7083e48e449.jpg\"}', 1, '2026-03-25 20:11:55', '2025-12-23 18:39:33', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (58, 3, 1, 2, 'TEXT', '【举报处理通知】您的内容（类型：post，ID：4）举报已成立，处理：删除/警告', 1, '2026-01-04 18:17:07', '2025-12-24 17:44:24', '2026-01-04 18:17:07', b'0');
INSERT INTO `messages` VALUES (59, 3, 1, 2, 'TEXT', '【举报处理通知】您的内容（类型：post，ID：3）举报已成立，处理：删除/警告。备注：123123', 1, '2026-01-04 18:17:07', '2025-12-24 17:54:27', '2026-01-04 18:17:07', b'0');
INSERT INTO `messages` VALUES (60, 1, 2, 3, 'TEXT', '321312', 1, '2026-02-25 18:44:12', '2025-12-26 17:31:13', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (61, 1, 3, 2, 'TEXT', '4324', 1, '2026-03-25 20:11:55', '2025-12-26 17:31:37', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (62, 1, 3, 2, 'TEXT', '4324', 1, '2026-03-25 20:11:55', '2025-12-26 17:31:41', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (63, 1, 3, 2, 'TEXT', '5', 1, '2026-03-25 20:11:55', '2025-12-26 17:31:44', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (64, 1, 3, 2, 'TEXT', '3', 1, '2026-03-25 20:11:55', '2025-12-26 17:34:09', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (65, 4, 4, 3, 'TEXT', '【系统消息】篮球迷 已接受你的好友请求，你们现在是好友了！', 1, '2026-02-25 18:43:31', '2025-12-29 16:12:52', '2026-02-25 18:43:31', b'0');
INSERT INTO `messages` VALUES (66, 4, 3, 4, 'TEXT', '👏', 1, '2025-12-29 16:20:31', '2025-12-29 16:20:31', '2025-12-29 16:20:31', b'0');
INSERT INTO `messages` VALUES (67, 1, 2, 3, 'TEXT', 'fdsf', 1, '2026-02-25 18:44:12', '2026-01-04 18:17:03', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (68, 1, 2, 3, 'TEXT', 'dsad', 1, '2026-02-25 18:44:12', '2026-01-04 18:17:10', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (69, 2, 2, 4, 'IMAGE', 'Chat Pictures/f45253db-86b5-4a1b-b4f6-3e9418e99e83.png', 0, NULL, '2026-02-05 17:34:44', '2026-02-05 17:34:44', b'0');
INSERT INTO `messages` VALUES (70, 1, 3, 2, 'TEXT', '111', 1, '2026-03-25 20:11:55', '2026-02-24 16:36:43', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (71, 1, 3, 2, 'SHARE_POST', '{\"type\":\"share_post\",\"postId\":7,\"author\":\"叶顺\",\"authorAvatar\":\"Profile Picture/33f18b56-fa81-4159-8e92-656c2a45b895.jpg\",\"createdAt\":\"2025-12-26T16:33:41\",\"content\":\" #跑步1\",\"image\":\"APicture/ef7dd89a-7183-46bc-b839-3581c8898654.png\"}', 1, '2026-03-25 20:11:55', '2026-02-24 16:37:26', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (72, 1, 2, 3, 'SHARE_POST', '{\"type\":\"share_post\",\"postId\":9,\"author\":\"叶顺\",\"authorAvatar\":\"Profile Picture/33f18b56-fa81-4159-8e92-656c2a45b895.jpg\",\"createdAt\":\"2025-12-26T16:37:55\",\"content\":\"1111\",\"image\":\"\"}', 1, '2026-02-25 18:44:12', '2026-02-25 17:29:33', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (73, 1, 2, 3, 'SHARE_POST', '{\"type\":\"share_post\",\"postId\":8,\"author\":\"叶顺\",\"authorAvatar\":\"Profile Picture/33f18b56-fa81-4159-8e92-656c2a45b895.jpg\",\"createdAt\":\"2025-12-26T16:34:36\",\"content\":\" #晨练 #健身餐 #乒乓球 #健身打卡 #增肌|\\n我是增肌王\",\"image\":\"\"}', 1, '2026-02-25 18:44:12', '2026-02-25 17:29:53', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (74, 1, 2, 3, 'TEXT', '211', 1, '2026-02-25 18:44:12', '2026-02-25 18:17:25', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (75, 2, 2, 4, 'IMAGE', 'Chat Pictures/57de0325-d020-4508-a5b0-1e5721e08e5a.png', 0, NULL, '2026-02-25 18:23:27', '2026-02-25 18:23:27', b'0');
INSERT INTO `messages` VALUES (76, 1, 2, 3, 'SHARE_POST', '{\"type\":\"share_post\",\"postId\":6,\"author\":\"叶顺\",\"authorAvatar\":\"Profile Picture/33f18b56-fa81-4159-8e92-656c2a45b895.jpg\",\"createdAt\":\"2025-12-26T16:17:51\",\"content\":\"eqw\",\"image\":\"\"}', 1, '2026-02-25 18:44:12', '2026-02-25 18:25:14', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (77, 1, 2, 3, 'SHARE_POST', '', 1, '2026-02-25 18:44:12', '2026-02-25 18:31:26', '2026-02-25 18:44:12', b'1');
INSERT INTO `messages` VALUES (78, 1, 3, 2, 'TEXT', '11111111111', 1, '2026-03-25 20:11:55', '2026-02-25 18:43:46', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (79, 1, 3, 2, 'TEXT', '111', 1, '2026-03-25 20:11:55', '2026-02-25 18:43:50', '2026-03-25 20:11:55', b'0');
INSERT INTO `messages` VALUES (80, 1, 3, 2, 'TEXT', '', 1, '2026-03-25 20:11:55', '2026-02-25 18:43:52', '2026-03-25 20:11:55', b'1');
INSERT INTO `messages` VALUES (81, 1, 2, 3, 'TEXT', 'dd', 1, '2026-02-25 18:44:12', '2026-02-25 18:44:12', '2026-02-25 18:44:12', b'0');
INSERT INTO `messages` VALUES (82, 1, 3, 2, 'TEXT', 'asd', 1, '2026-03-25 20:11:55', '2026-02-25 18:44:18', '2026-03-25 20:11:55', b'0');

-- ----------------------------
-- Table structure for post_images
-- ----------------------------
DROP TABLE IF EXISTS `post_images`;
CREATE TABLE `post_images`  (
  `post_id` bigint NOT NULL COMMENT '动态ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`post_id`, `image_url`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `post_images_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动态图片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_images
-- ----------------------------
INSERT INTO `post_images` VALUES (1, 'https://picsum.photos/400/300?random=20');
INSERT INTO `post_images` VALUES (2, 'https://picsum.photos/400/300?random=21');
INSERT INTO `post_images` VALUES (7, 'APicture/ef7dd89a-7183-46bc-b839-3581c8898654.png');

-- ----------------------------
-- Table structure for post_likes
-- ----------------------------
DROP TABLE IF EXISTS `post_likes`;
CREATE TABLE `post_likes`  (
  `post_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`post_id`, `user_id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `post_likes_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动态点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_likes
-- ----------------------------
INSERT INTO `post_likes` VALUES (1, 3);
INSERT INTO `post_likes` VALUES (8, 2);
INSERT INTO `post_likes` VALUES (9, 2);

-- ----------------------------
-- Table structure for post_mentions
-- ----------------------------
DROP TABLE IF EXISTS `post_mentions`;
CREATE TABLE `post_mentions`  (
  `post_id` bigint NOT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  INDEX `FKhcl6osasm99cmo3ek9s94cbbb`(`post_id` ASC) USING BTREE,
  CONSTRAINT `FKhcl6osasm99cmo3ek9s94cbbb` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_mentions
-- ----------------------------

-- ----------------------------
-- Table structure for post_topics
-- ----------------------------
DROP TABLE IF EXISTS `post_topics`;
CREATE TABLE `post_topics`  (
  `post_id` bigint NOT NULL COMMENT '动态ID',
  `topic_id` bigint NOT NULL COMMENT '话题ID',
  PRIMARY KEY (`post_id`, `topic_id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_topic_id`(`topic_id` ASC) USING BTREE,
  CONSTRAINT `post_topics_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_topics_ibfk_2` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动态-话题关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_topics
-- ----------------------------
INSERT INTO `post_topics` VALUES (5, 1);
INSERT INTO `post_topics` VALUES (7, 2);
INSERT INTO `post_topics` VALUES (7, 3);
INSERT INTO `post_topics` VALUES (8, 4);
INSERT INTO `post_topics` VALUES (8, 5);
INSERT INTO `post_topics` VALUES (8, 6);
INSERT INTO `post_topics` VALUES (8, 7);
INSERT INTO `post_topics` VALUES (8, 8);
INSERT INTO `post_topics` VALUES (8, 9);
INSERT INTO `post_topics` VALUES (9, 4);
INSERT INTO `post_topics` VALUES (9, 5);
INSERT INTO `post_topics` VALUES (9, 10);
INSERT INTO `post_topics` VALUES (9, 11);
INSERT INTO `post_topics` VALUES (9, 12);
INSERT INTO `post_topics` VALUES (10, 2);
INSERT INTO `post_topics` VALUES (12, 10);

-- ----------------------------
-- Table structure for posts
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `video` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `venue_id` bigint NULL DEFAULT NULL COMMENT '关联场馆ID',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论数',
  `is_pinned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `is_featured` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否精华',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_is_pinned`(`is_pinned` ASC) USING BTREE,
  INDEX `idx_is_featured`(`is_featured` ASC) USING BTREE,
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `posts_ibfk_2` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '动态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of posts
-- ----------------------------
INSERT INTO `posts` VALUES (1, 2, '今天在武汉晴川学院篮球场打球，好热血！', NULL, NULL, 1, 0, 0, '2025-12-15 17:06:48', '2025-12-15 17:06:48');
INSERT INTO `posts` VALUES (2, 3, '羽毛球场的空调太舒服了！', NULL, NULL, 0, 0, 0, '2025-12-15 17:06:48', '2025-12-15 17:06:48');
INSERT INTO `posts` VALUES (5, 2, '#今天天气\nsadas', NULL, NULL, 0, 0, 0, '2025-12-26 16:11:57', '2025-12-26 16:11:57');
INSERT INTO `posts` VALUES (6, 2, 'eqw', 'AVideo/e1e69391-38fd-4179-b632-48b4d7180024.mp4', 1, 0, 0, 0, '2025-12-26 16:17:51', '2025-12-26 16:17:51');
INSERT INTO `posts` VALUES (7, 2, ' #跑步1', 'AVideo/3eefa84e-48c7-4b85-8350-738f4f4b0ef8.mp4', 1, 0, 0, 0, '2025-12-26 16:33:41', '2025-12-26 16:33:41');
INSERT INTO `posts` VALUES (8, 2, ' #晨练 #健身餐 #乒乓球 #健身打卡 #增肌|\n我是增肌王', NULL, NULL, 4, 0, 0, '2025-12-26 16:34:36', '2025-12-26 17:29:57');
INSERT INTO `posts` VALUES (9, 2, '1111', NULL, NULL, 0, 0, 1, '2025-12-26 16:37:55', '2025-12-26 17:39:31');
INSERT INTO `posts` VALUES (10, 2, '@Ⅳ ', NULL, NULL, 0, 0, 0, '2025-12-27 17:44:10', '2025-12-27 17:44:10');
INSERT INTO `posts` VALUES (11, 2, '@篮球迷  sada', NULL, NULL, 0, 0, 0, '2025-12-27 17:45:44', '2025-12-27 17:45:44');
INSERT INTO `posts` VALUES (12, 2, '@Ⅳ ', NULL, NULL, 0, 0, 0, '2025-12-27 17:47:21', '2025-12-27 17:47:21');
INSERT INTO `posts` VALUES (13, 2, '@篮球迷 ', NULL, NULL, 0, 0, 0, '2025-12-27 17:48:55', '2025-12-27 17:48:55');

-- ----------------------------
-- Table structure for report_images
-- ----------------------------
DROP TABLE IF EXISTS `report_images`;
CREATE TABLE `report_images`  (
  `report_id` bigint NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  INDEX `FKio33xl5nyhe7fv6e8me83ddj5`(`report_id` ASC) USING BTREE,
  CONSTRAINT `FKio33xl5nyhe7fv6e8me83ddj5` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of report_images
-- ----------------------------
INSERT INTO `report_images` VALUES (4, 'APicture/1ff5fc4c-33a7-455a-8418-603126fff060.jpg');

-- ----------------------------
-- Table structure for reports
-- ----------------------------
DROP TABLE IF EXISTS `reports`;
CREATE TABLE `reports`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `action` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `admin_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `content_removed` bit(1) NOT NULL,
  `handled_at` datetime(6) NULL DEFAULT NULL,
  `reason` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `target_id` bigint NOT NULL,
  `target_type` enum('COMMENT','POST') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `handler_id` bigint NULL DEFAULT NULL,
  `reported_user_id` bigint NOT NULL,
  `reporter_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKe0raduri876fn5l4ykqqg9i73`(`handler_id` ASC) USING BTREE,
  INDEX `FKb3bqi44mjskbnwupr31nfq5ui`(`reported_user_id` ASC) USING BTREE,
  INDEX `FKd3qiw2om5d2oh5xb7fbdcq225`(`reporter_id` ASC) USING BTREE,
  CONSTRAINT `FKb3bqi44mjskbnwupr31nfq5ui` FOREIGN KEY (`reported_user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKd3qiw2om5d2oh5xb7fbdcq225` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKe0raduri876fn5l4ykqqg9i73` FOREIGN KEY (`handler_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reports
-- ----------------------------
INSERT INTO `reports` VALUES (1, '2025-12-24 17:09:50.704532', '2025-12-24 17:44:24.230503', '删除/警告', NULL, b'1', '2025-12-24 17:44:24.215554', '1111', 'APPROVED', '23123231231', 4, 'POST', 1, 2, 3);
INSERT INTO `reports` VALUES (2, '2025-12-24 17:39:34.517210', '2025-12-24 17:39:34.517210', NULL, NULL, b'0', NULL, '1', 'PENDING', '1212', 3, 'COMMENT', NULL, 2, 2);
INSERT INTO `reports` VALUES (3, '2025-12-24 17:39:38.422077', '2025-12-24 17:39:38.422077', NULL, NULL, b'0', NULL, '1', 'PENDING', '1212', 3, 'COMMENT', NULL, 2, 2);
INSERT INTO `reports` VALUES (4, '2025-12-24 17:53:30.191385', '2025-12-24 17:54:27.012794', '删除/警告', '123123', b'1', '2025-12-24 17:54:27.008178', '1111', 'APPROVED', 'good\n', 3, 'POST', 1, 2, 2);

-- ----------------------------
-- Table structure for school_options
-- ----------------------------
DROP TABLE IF EXISTS `school_options`;
CREATE TABLE `school_options`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学校名称',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用 1=可选 0=不可选',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序值，越大越靠前',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_school_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '注册学校下拉选项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of school_options
-- ----------------------------
INSERT INTO `school_options` VALUES (1, '武汉晴川学院', 1, 100, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (2, '武汉大学', 1, 90, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (3, '华中科技大学', 1, 80, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (4, '武汉理工大学', 1, 70, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (5, '华中师范大学', 1, 60, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (6, '中南财经政法大学', 1, 50, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (7, '中国地质大学（武汉）', 1, 40, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (8, '华中农业大学', 1, 30, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (9, '武汉科技大学', 1, 20, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (10, '湖北大学', 1, 10, '2026-03-04 18:39:54', '2026-03-04 18:39:54');
INSERT INTO `school_options` VALUES (11, '其他', 1, 0, '2026-03-04 18:39:54', '2026-03-04 18:39:54');

-- ----------------------------
-- Table structure for topics
-- ----------------------------
DROP TABLE IF EXISTS `topics`;
CREATE TABLE `topics`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '话题名称（不含#号）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '话题描述',
  `post_count` int NOT NULL DEFAULT 0 COMMENT '动态数量',
  `subscriber_count` int NOT NULL DEFAULT 0 COMMENT '订阅者数量',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_post_count`(`post_count` ASC) USING BTREE,
  INDEX `idx_subscriber_count`(`subscriber_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '话题表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topics
-- ----------------------------
INSERT INTO `topics` VALUES (1, '今天天气', NULL, 1, 0, '2025-12-26 16:11:57', '2025-12-26 16:11:57');
INSERT INTO `topics` VALUES (2, '跑步', NULL, 2, 0, '2025-12-26 16:33:41', '2025-12-27 17:44:10');
INSERT INTO `topics` VALUES (3, '跑步1', NULL, 1, 0, '2025-12-26 16:33:41', '2025-12-26 16:33:41');
INSERT INTO `topics` VALUES (4, '晨练', NULL, 2, 0, '2025-12-26 16:34:36', '2025-12-26 16:37:55');
INSERT INTO `topics` VALUES (5, '健身餐', NULL, 2, 0, '2025-12-26 16:34:36', '2025-12-26 16:37:55');
INSERT INTO `topics` VALUES (6, '乒乓球', NULL, 1, 0, '2025-12-26 16:34:36', '2025-12-26 16:34:36');
INSERT INTO `topics` VALUES (7, '健身打卡', NULL, 1, 0, '2025-12-26 16:34:36', '2025-12-26 16:34:36');
INSERT INTO `topics` VALUES (8, '增肌', NULL, 1, 0, '2025-12-26 16:34:36', '2025-12-26 16:34:36');
INSERT INTO `topics` VALUES (9, '增肌|', NULL, 1, 0, '2025-12-26 16:34:36', '2025-12-26 16:34:36');
INSERT INTO `topics` VALUES (10, '力量训练', NULL, 2, 0, '2025-12-26 16:37:55', '2025-12-27 17:47:21');
INSERT INTO `topics` VALUES (11, '瑜伽', NULL, 1, 0, '2025-12-26 16:37:55', '2025-12-26 16:37:55');
INSERT INTO `topics` VALUES (12, '运动心得', NULL, 1, 0, '2025-12-26 16:37:55', '2025-12-26 16:37:55');

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`, `role_name`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES (1, 'ROLE_ADMIN');
INSERT INTO `user_roles` VALUES (1, 'ROLE_USER');
INSERT INTO `user_roles` VALUES (2, 'ROLE_USER');
INSERT INTO `user_roles` VALUES (3, 'ROLE_USER');
INSERT INTO `user_roles` VALUES (4, 'ROLE_USER');
INSERT INTO `user_roles` VALUES (5, 'ROLE_USER');
INSERT INTO `user_roles` VALUES (6, 'ROLE_USER');
INSERT INTO `user_roles` VALUES (7, 'ROLE_USER');
INSERT INTO `user_roles` VALUES (8, 'ROLE_USER');
INSERT INTO `user_roles` VALUES (9, 'ROLE_USER');

-- ----------------------------
-- Table structure for user_topic_subscriptions
-- ----------------------------
DROP TABLE IF EXISTS `user_topic_subscriptions`;
CREATE TABLE `user_topic_subscriptions`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `topic_id` bigint NOT NULL COMMENT '话题ID',
  `created_at` datetime NOT NULL COMMENT '订阅时间',
  PRIMARY KEY (`user_id`, `topic_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_topic_id`(`topic_id` ASC) USING BTREE,
  CONSTRAINT `user_topic_subscriptions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_topic_subscriptions_ibfk_2` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-话题订阅表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_topic_subscriptions
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `is_student` tinyint(1) NULL DEFAULT NULL COMMENT '是否为学生',
  `school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学校',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像路径',
  `bio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人简介',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别: MALE, FEMALE, OTHER',
  `is_admin` tinyint(1) NULL DEFAULT NULL COMMENT '是否为管理员',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `student_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account`(`account` ASC) USING BTREE,
  INDEX `idx_account`(`account` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '1001', '$2a$10$t/uJ2j6ZmO/H8GqY87d3CuH1P0RtcLXhc7MXjr.6B7yN2E5Mcnjgm', '管理员', 'admin@example.com', '13800138001', NULL, NULL, '超级管理员', NULL, NULL, 'MALE', 1, '2025-12-15 17:06:48', '2025-12-15 17:06:48', NULL);
INSERT INTO `users` VALUES (2, '1002', '$2a$10$sA1G8jTVLSqa5jDcjWjdMOEzznk0orNzpjx75k24uKZCNhUUUSDKi', '叶顺', '2787721585@qq.com', '13800138002', 1, '武汉晴川学院', '似是秋风', 'Profile Picture/33f18b56-fa81-4159-8e92-656c2a45b895.jpg', NULL, 'MALE', 0, '2025-12-15 17:06:48', '2025-12-23 17:46:37', NULL);
INSERT INTO `users` VALUES (3, '1003', '$2a$10$VGhc4G7/pFZxCsCOH7ghXeQmCASRjvOPomE1dGPcnts9rLXat8j4O', '李四', 'lisi@example.com', '13800138003', 1, '武汉大学', 'Ⅳ', 'Profile Picture/3b6b732a-eaab-4a4a-a3a5-47b9e1be34e1.png', NULL, 'MALE', NULL, '2025-12-15 17:06:48', '2025-12-23 17:44:08', NULL);
INSERT INTO `users` VALUES (4, '1004', '$2a$10$dhEjaCC8gT0IehzWaBh2Uuug/X8WWt7ApBqaiqp68FoC/NnFchKQq', '王五', 'wangwu@example.com', '13800138004', NULL, '华中科技大学', '篮球迷', NULL, NULL, 'MALE', NULL, '2025-12-15 17:06:48', '2025-12-15 17:06:48', NULL);
INSERT INTO `users` VALUES (5, '1005', '$2a$10$erykUpaFpOBBL9cjE2soxusV4MALNsE/QxLVt.yyf1vMgABfdgcJO', '赵六', 'zhaoliu@example.com', '13800138005', NULL, '武汉大学', '游泳健将', NULL, NULL, 'MALE', NULL, '2025-12-15 17:06:48', '2025-12-15 17:06:48', NULL);
INSERT INTO `users` VALUES (6, '1007', '$2a$10$eF.TRHnVVWd6uzLvgEwpTeCWZWxyh7LdL5qJJegNilzaWjBtdi9y2', 'ue', '2787721585@qq.com', '15072093823', 1, '武汉理工大学', '111', 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'200\' height=\'200\' viewBox=\'0 0 200 200\'%3E%3Crect width=\'200\' height=\'200\' fill=\'%23CCCCCC\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' font-size=\'100\' text-anchor=\'middle\' dominant-baseline=\'middle\' fill=\'%23999999\'%3E👤%3C/text%3E%3C/svg%3E', NULL, 'MALE', 0, '2025-12-26 18:53:34', '2025-12-26 18:55:35', NULL);
INSERT INTO `users` VALUES (7, '1234', '$2a$10$0pYEK4vmDDKi1aOmk4OdPuk78ro32ua4iy7bZDgh42RMXqACbJZHC', '111', '12312@qq.com', '15072093823', 0, '武汉大学', '111', 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'200\' height=\'200\' viewBox=\'0 0 200 200\'%3E%3Crect width=\'200\' height=\'200\' fill=\'%23CCCCCC\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' font-size=\'100\' text-anchor=\'middle\' dominant-baseline=\'middle\' fill=\'%23999999\'%3E👤%3C/text%3E%3C/svg%3E', NULL, 'MALE', 0, '2025-12-29 16:41:19', '2025-12-29 16:41:19', '12312412412');
INSERT INTO `users` VALUES (8, '1000', '$2a$10$8KeyMh.OB0IZFLNBJ5WS3uJfhpE4B.HVjcn2Dg744ugRBXF1ItxyW', '管理员', '123@qq.com', '15072093823', 0, '', '管理员', 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'200\' height=\'200\' viewBox=\'0 0 200 200\'%3E%3Crect width=\'200\' height=\'200\' fill=\'%23CCCCCC\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' font-size=\'100\' text-anchor=\'middle\' dominant-baseline=\'middle\' fill=\'%23999999\'%3E👤%3C/text%3E%3C/svg%3E', NULL, 'MALE', 0, '2025-12-29 17:10:39', '2025-12-29 17:10:39', NULL);
INSERT INTO `users` VALUES (9, '10003', '$2a$10$LzvFbbqB3I1UbVZKd2XSfOuiYkpwHZgmd0uPxJRWdGGUHY7OfaGCq', '咳咳', '2787721585@qq.com', '14972093823', 1, '武汉大学', '咳咳', 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'200\' height=\'200\' viewBox=\'0 0 200 200\'%3E%3Crect width=\'200\' height=\'200\' fill=\'%23CCCCCC\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' font-size=\'100\' text-anchor=\'middle\' dominant-baseline=\'middle\' fill=\'%23999999\'%3E👤%3C/text%3E%3C/svg%3E', NULL, 'MALE', 0, '2026-03-25 20:36:32', '2026-03-25 20:36:32', '20220402034');

-- ----------------------------
-- Table structure for venue_favorites
-- ----------------------------
DROP TABLE IF EXISTS `venue_favorites`;
CREATE TABLE `venue_favorites`  (
  `venue_id` bigint NOT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  INDEX `FKs3wow3nhpdoof6hwenr26pgei`(`venue_id` ASC) USING BTREE,
  INDEX `FK4m6ok2of6gpxyhcv2j44yt3qq`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK4m6ok2of6gpxyhcv2j44yt3qq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKs3wow3nhpdoof6hwenr26pgei` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venue_favorites
-- ----------------------------

-- ----------------------------
-- Table structure for venue_open_reminders
-- ----------------------------
DROP TABLE IF EXISTS `venue_open_reminders`;
CREATE TABLE `venue_open_reminders`  (
  `venue_id` bigint NOT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  INDEX `FKicxqcmx0lxt34p6pv8775essq`(`venue_id` ASC) USING BTREE,
  CONSTRAINT `FKicxqcmx0lxt34p6pv8775essq` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venue_open_reminders
-- ----------------------------

-- ----------------------------
-- Table structure for venue_review_images
-- ----------------------------
DROP TABLE IF EXISTS `venue_review_images`;
CREATE TABLE `venue_review_images`  (
  `review_id` bigint NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  INDEX `FKou4ojdbluw8s5oqego8vc7hv3`(`review_id` ASC) USING BTREE,
  CONSTRAINT `FKou4ojdbluw8s5oqego8vc7hv3` FOREIGN KEY (`review_id`) REFERENCES `venue_reviews` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venue_review_images
-- ----------------------------
INSERT INTO `venue_review_images` VALUES (1, NULL, 'BPicture/8daf8e2f-815b-4a04-bd0c-62d35971ff1d.png');

-- ----------------------------
-- Table structure for venue_review_videos
-- ----------------------------
DROP TABLE IF EXISTS `venue_review_videos`;
CREATE TABLE `venue_review_videos`  (
  `review_id` bigint NOT NULL COMMENT '评论ID',
  `video_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`review_id`, `video_path`) USING BTREE,
  INDEX `idx_review_id`(`review_id` ASC) USING BTREE,
  CONSTRAINT `venue_review_videos_ibfk_1` FOREIGN KEY (`review_id`) REFERENCES `venue_reviews` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '场馆评论视频表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venue_review_videos
-- ----------------------------

-- ----------------------------
-- Table structure for venue_reviews
-- ----------------------------
DROP TABLE IF EXISTS `venue_reviews`;
CREATE TABLE `venue_reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `rating` int NULL DEFAULT NULL,
  `video` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `venue_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKgpht1umbx9l5wxudmdekn7qtu`(`user_id` ASC) USING BTREE,
  INDEX `FKsmijcdi2o83yumr3q5uhn1x5d`(`venue_id` ASC) USING BTREE,
  CONSTRAINT `FKgpht1umbx9l5wxudmdekn7qtu` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKsmijcdi2o83yumr3q5uhn1x5d` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venue_reviews
-- ----------------------------
INSERT INTO `venue_reviews` VALUES (1, '2025-12-26 18:08:01.996648', '2025-12-26 18:08:01.996648', 'feichanghao1', 4, NULL, 2, 1);
INSERT INTO `venue_reviews` VALUES (2, '2025-12-26 18:08:41.448450', '2025-12-26 18:08:41.448450', 'npo', 1, NULL, 3, 1);

-- ----------------------------
-- Table structure for venues
-- ----------------------------
DROP TABLE IF EXISTS `venues`;
CREATE TABLE `venues`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场馆名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型',
  `capacity` int NULL DEFAULT NULL COMMENT '容纳人数',
  `price` double NULL DEFAULT NULL COMMENT '价格/小时',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `open_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开放时间',
  `close_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关闭时间',
  `school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属学校',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_school`(`school` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '场馆表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venues
-- ----------------------------
INSERT INTO `venues` VALUES (1, '武汉晴川学院体育馆', '武汉市东湖新技术开发区', '综合体育馆', 200, 50, 'Venue Pictures/f0e4962e-0fe3-44f2-920e-8b79a1477ef6.jpg', '设备齐全，环境优雅，面向本校学生开放', '06:00', '22:00', '武汉晴川学院', '2025-12-15 17:06:48', '2025-12-15 17:52:35');

SET FOREIGN_KEY_CHECKS = 1;
