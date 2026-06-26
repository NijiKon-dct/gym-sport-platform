package com.example.gymbackend.config;

import com.example.gymbackend.model.*;
import com.example.gymbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
        }
        if (venueRepository.count() == 0) {
            seedVenues();
        }
        if (bookingRepository.count() == 0) {
            seedBookings();
        }
        if (postRepository.count() == 0) {
            seedPostsAndComments();
        }
        if (friendshipRepository.count() == 0) {
            seedFriendships();
        }
        if (chatSessionRepository.count() == 0) {
            seedChats();
        }
    }

    private void seedUsers() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .account("1001").username("管理员").password(passwordEncoder.encode("123456"))
                .email("admin@example.com").phone("13800138001").nickname("超级管理员").isAdmin(true)
                .avatar(null)
                .roles(Set.of("ROLE_ADMIN", "ROLE_USER"))
                .build());
        users.add(User.builder()
                .account("1002").username("张三").password(passwordEncoder.encode("123456"))
                .email("zhangsan@example.com").phone("13800138002").school("武汉晴川学院").isStudent(true)
                .nickname("运动达人").avatar(null)
                .roles(Set.of("ROLE_USER"))
                .build());
        users.add(User.builder()
                .account("1003").username("李四").password(passwordEncoder.encode("123456"))
                .email("lisi@example.com").phone("13800138003").school("武汉大学").isStudent(true)
                .nickname("羽毛球爱好者").avatar(null)
                .roles(Set.of("ROLE_USER"))
                .build());
        users.add(User.builder()
                .account("1004").username("王五").password(passwordEncoder.encode("123456"))
                .email("wangwu@example.com").phone("13800138004").school("华中科技大学")
                .nickname("篮球迷").avatar(null)
                .roles(Set.of("ROLE_USER"))
                .build());
        users.add(User.builder()
                .account("1005").username("赵六").password(passwordEncoder.encode("123456"))
                .email("zhaoliu@example.com").phone("13800138005").school("武汉大学")
                .nickname("游泳健将").avatar(null)
                .roles(Set.of("ROLE_USER"))
                .build());
        userRepository.saveAll(users);
    }

    private void seedVenues() {
        List<Venue> venues = List.of(
                Venue.builder().name("武汉晴川学院体育馆").address("武汉市东湖新技术开发区")
                        .type("综合体育馆").capacity(200).price(50.0)
                        .image("https://picsum.photos/400/200?random=1")
                        .description("设备齐全，环境优雅，面向本校学生开放")
                        .openTime("06:00").closeTime("22:00").school("武汉晴川学院").build(),
                Venue.builder().name("武汉晴川学院篮球场").address("武汉市东湖新技术开发区")
                        .type("篮球场").capacity(50).price(30.0)
                        .image("https://picsum.photos/400/200?random=2")
                        .description("标准室内篮球场").openTime("08:00").closeTime("21:00")
                        .school("武汉晴川学院").build(),
                Venue.builder().name("武汉晴川学院羽毛球场").address("武汉市东湖新技术开发区")
                        .type("羽毛球场").capacity(40).price(25.0)
                        .image("https://picsum.photos/400/200?random=3")
                        .description("专业羽毛球场地").openTime("07:00").closeTime("23:00")
                        .school("武汉晴川学院").build(),
                Venue.builder().name("武汉大学体育馆").address("武汉市武昌区珞珈山")
                        .type("综合体育馆").capacity(300).price(60.0)
                        .image("https://picsum.photos/400/200?random=4")
                        .description("武汉大学重点场馆").openTime("06:00").closeTime("22:00")
                        .school("武汉大学").build(),
                Venue.builder().name("武汉大学游泳馆").address("武汉市武昌区珞珈山")
                        .type("游泳池").capacity(100).price(40.0)
                        .image("https://picsum.photos/400/200?random=5")
                        .description("恒温游泳池").openTime("06:00").closeTime("22:00")
                        .school("武汉大学").build(),
                Venue.builder().name("华中科技大学体育馆").address("武汉市洪山区珞喻路")
                        .type("综合体育馆").capacity(250).price(55.0)
                        .image("https://picsum.photos/400/200?random=6")
                        .description("华中科技大学").openTime("06:00").closeTime("22:00")
                        .school("华中科技大学").build(),
                Venue.builder().name("武汉市体育中心").address("武汉市汉阳区")
                        .type("综合体育馆").capacity(500).price(100.0)
                        .image("https://picsum.photos/400/200?random=7")
                        .description("大型综合体育馆").openTime("08:00").closeTime("22:00")
                        .build(),
                Venue.builder().name("光谷体育馆").address("武汉市东湖新技术开发区")
                        .type("篮球场").capacity(100).price(80.0)
                        .image("https://picsum.photos/400/200?random=8")
                        .description("标准室内篮球场").openTime("08:00").closeTime("21:00")
                        .build(),
                Venue.builder().name("武汉体育学院健身房").address("武汉市洪山区珞瑜路")
                        .type("健身房").capacity(80).price(45.0)
                        .image("https://picsum.photos/400/200?random=9")
                        .description("综合健身房").openTime("06:00").closeTime("23:00")
                        .build(),
                Venue.builder().name("汉阳国际网球中心").address("武汉市汉阳区江城大道")
                        .type("网球场").capacity(40).price(70.0)
                        .image("https://picsum.photos/400/200?random=10")
                        .description("室内恒温网球馆").openTime("07:00").closeTime("22:00")
                        .build()
        );
        venueRepository.saveAll(venues);
    }

    private void seedBookings() {
        User user2 = userRepository.findByAccount("1002").orElseThrow();
        User user3 = userRepository.findByAccount("1003").orElseThrow();
        User user4 = userRepository.findByAccount("1004").orElseThrow();
        List<Venue> venues = venueRepository.findAll();

        List<Booking> bookings = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 只使用可用的场馆索引，防止数组越界
        if (venues.size() > 0) {
            bookings.add(Booking.builder()
                    .user(user2).venue(venues.get(0)).date(today)
                    .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(11, 0))
                    .status(BookingStatus.CONFIRMED).price(venues.get(0).getPrice()).build());
        }
        if (venues.size() > 1) {
            bookings.add(Booking.builder()
                    .user(user2).venue(venues.get(1)).date(today.plusDays(1))
                    .startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0))
                    .status(BookingStatus.PENDING).price(venues.get(1).getPrice()).build());
        }
        if (venues.size() > 2) {
            bookings.add(Booking.builder()
                    .user(user3).venue(venues.get(2)).date(today.minusDays(1))
                    .startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0))
                    .status(BookingStatus.CONFIRMED).price(venues.get(2).getPrice()).build());
        }
        if (venues.size() > 3) {
            bookings.add(Booking.builder()
                    .user(user4).venue(venues.get(3)).date(today.minusDays(2))
                    .startTime(LocalTime.of(18, 0)).endTime(LocalTime.of(20, 0))
                    .status(BookingStatus.CONFIRMED).price(venues.get(3).getPrice()).build());
        }
        bookingRepository.saveAll(bookings);
    }

    private void seedPostsAndComments() {
        User user2 = userRepository.findByAccount("1002").orElseThrow();
        User user3 = userRepository.findByAccount("1003").orElseThrow();
        User user4 = userRepository.findByAccount("1004").orElseThrow();
        Venue venue2 = venueRepository.findAll().get(1);
        Venue venue3 = venueRepository.findAll().get(2);

        Post post1 = Post.builder()
                .user(user2)
                .content("今天在武汉晴川学院篮球场打球，好热血！")
                .images(List.of("https://picsum.photos/400/300?random=20"))
                .venue(venue2)
                .build();
        post1.getLikedUserIds().add(user3.getId());

        Post post2 = Post.builder()
                .user(user3)
                .content("羽毛球场的空调太舒服了！")
                .images(List.of("https://picsum.photos/400/300?random=21"))
                .venue(venue3)
                .build();
        post2.getLikedUserIds().add(user2.getId());

        postRepository.saveAll(List.of(post1, post2));

        Comment comment = Comment.builder()
                .post(post1)
                .user(user4)
                .content("下次带上我！")
                .build();
        commentRepository.save(comment);
        post1.setCommentCount(1);
        postRepository.save(post1);
    }

    private void seedFriendships() {
        User user2 = userRepository.findByAccount("1002").orElseThrow();
        User user3 = userRepository.findByAccount("1003").orElseThrow();
        User user4 = userRepository.findByAccount("1004").orElseThrow();

        createFriendshipPair(user2, user3);
        createFriendshipPair(user2, user4);

        FriendRequest request = FriendRequest.builder()
                .fromUser(user3)
                .toUser(user4)
                .status(FriendRequestStatus.PENDING)
                .message("一起运动吗？")
                .build();
        friendRequestRepository.save(request);
    }

    private void createFriendshipPair(User a, User b) {
        Friendship ab = Friendship.builder()
                .user(a).friend(b).isOnline(true).lastSeen(LocalDateTime.now().minusMinutes(30))
                .build();
        Friendship ba = Friendship.builder()
                .user(b).friend(a).isOnline(false).lastSeen(LocalDateTime.now().minusHours(2))
                .build();
        friendshipRepository.save(ab);
        friendshipRepository.save(ba);
    }

    private void seedChats() {
        User user2 = userRepository.findByAccount("1002").orElseThrow();
        User user3 = userRepository.findByAccount("1003").orElseThrow();

        ChatSession chat = chatSessionRepository.save(ChatSession.builder()
                .userA(user2)
                .userB(user3)
                .lastMessage("好的，明天见！")
                .lastMessageTime(LocalDateTime.now().minusHours(2))
                .unreadCountForUserA(0)
                .unreadCountForUserB(1)
                .build());

        messageRepository.save(Message.builder()
                .chat(chat)
                .sender(user3)
                .receiver(user2)
                .type(MessageType.TEXT)
                .content("明天一起打羽毛球吧！")
                .isRead(true)
                .build());

        messageRepository.save(Message.builder()
                .chat(chat)
                .sender(user2)
                .receiver(user3)
                .type(MessageType.TEXT)
                .content("好的，明天见！")
                .isRead(false)
                .build());
    }
}

