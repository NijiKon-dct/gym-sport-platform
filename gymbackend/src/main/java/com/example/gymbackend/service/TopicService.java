package com.example.gymbackend.service;

import com.example.gymbackend.dto.topic.TopicResponse;
import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.Topic;
import com.example.gymbackend.model.User;
import com.example.gymbackend.repository.TopicRepository;
import com.example.gymbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TopicResponse> listTopics(String keyword, Long currentUserId) {
        List<Topic> topics;
        if (keyword != null && !keyword.trim().isEmpty()) {
            topics = topicRepository.searchByName(keyword.trim());
        } else {
            topics = topicRepository.findAllOrderedByPopularity();
        }
        return topics.stream()
                .map(topic -> toTopicResponse(topic, currentUserId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> listSubscribedTopics(Long userId) {
        List<Topic> topics = topicRepository.findBySubscriberId(userId);
        return topics.stream()
                .map(topic -> toTopicResponse(topic, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public TopicResponse getTopic(Long topicId, Long currentUserId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("话题不存在"));
        return toTopicResponse(topic, currentUserId);
    }

    @Transactional(readOnly = true)
    public TopicResponse getTopicByName(String topicName, Long currentUserId) {
        Topic topic = topicRepository.findByName(topicName)
                .orElseThrow(() -> new ResourceNotFoundException("话题不存在"));
        return toTopicResponse(topic, currentUserId);
    }

    public TopicResponse createOrGetTopic(String topicName) {
        // 移除#号（如果有）
        String rawName = topicName.startsWith("#") ? topicName.substring(1) : topicName;
        final String name = rawName.trim();

        Topic topic = topicRepository.findByName(name)
                .orElseGet(() -> {
                    Topic newTopic = Topic.builder()
                            .name(name)
                            .postCount(0)
                            .subscriberCount(0)
                            .build();
                    return topicRepository.save(newTopic);
                });
        return toTopicResponse(topic, null);
    }

    public Set<Topic> createOrGetTopics(List<String> topicNames) {
        if (topicNames == null || topicNames.isEmpty()) {
            return Set.of();
        }
        return topicNames.stream()
                .map(this::createOrGetTopicByName)
                .collect(Collectors.toSet());
    }

    private Topic createOrGetTopicByName(String topicName) {
        String rawName = topicName.startsWith("#") ? topicName.substring(1) : topicName;
        final String name = rawName.trim();
        return topicRepository.findByName(name)
                .orElseGet(() -> {
                    Topic newTopic = Topic.builder()
                            .name(name)
                            .postCount(0)
                            .subscriberCount(0)
                            .build();
                    return topicRepository.save(newTopic);
                });
    }

    public void incrementPostCount(Set<Topic> topics) {
        topics.forEach(topic -> {
            topic.setPostCount(topic.getPostCount() + 1);
            topicRepository.save(topic);
        });
    }

    public void decrementPostCount(Set<Topic> topics) {
        topics.forEach(topic -> {
            topic.setPostCount(Math.max(0, topic.getPostCount() - 1));
            topicRepository.save(topic);
        });
    }

    public TopicResponse subscribeTopic(Long topicId, Long userId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("话题不存在"));
        User user = userService.findById(userId);

        if (!topic.getSubscribers().contains(user)) {
            topic.getSubscribers().add(user);
            user.getSubscribedTopics().add(topic);
            topic.setSubscriberCount(topic.getSubscriberCount() + 1);
            topicRepository.save(topic);
            userRepository.save(user);
        }

        return toTopicResponse(topic, userId);
    }

    public TopicResponse unsubscribeTopic(Long topicId, Long userId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("话题不存在"));
        User user = userService.findById(userId);

        if (topic.getSubscribers().contains(user)) {
            topic.getSubscribers().remove(user);
            user.getSubscribedTopics().remove(topic);
            topic.setSubscriberCount(Math.max(0, topic.getSubscriberCount() - 1));
            topicRepository.save(topic);
            userRepository.save(user);
        }

        return toTopicResponse(topic, userId);
    }

    private TopicResponse toTopicResponse(Topic topic, Long currentUserId) {
        boolean isSubscribed = false;
        if (currentUserId != null) {
            User user = userService.findById(currentUserId);
            isSubscribed = topic.getSubscribers().contains(user);
        }
        return new TopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getDescription(),
                topic.getPostCount(),
                topic.getSubscriberCount(),
                isSubscribed,
                topic.getCreatedAt()
        );
    }
}

