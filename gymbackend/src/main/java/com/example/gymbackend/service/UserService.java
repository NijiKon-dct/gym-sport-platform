package com.example.gymbackend.service;

import com.example.gymbackend.dto.auth.AuthResponse;
import com.example.gymbackend.dto.auth.LoginRequest;
import com.example.gymbackend.dto.auth.RegisterRequest;
import com.example.gymbackend.dto.user.UpdateUserRequest;
import com.example.gymbackend.dto.user.UserDto;
import com.example.gymbackend.exception.BadRequestException;
import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.User;
import com.example.gymbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByAccount(request.account())
                .orElseThrow(() -> new ResourceNotFoundException("账号不存在"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("账号或密码错误");
        }
        // TODO: replace with real JWT if needed
        String token = "mock-token-" + UUID.randomUUID();
        UserDto userDto = toDto(user);
        log.debug("用户登录: account={}, avatar={}", request.account(), userDto.avatar());
        return new AuthResponse(token, userDto);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByAccount(request.account())) {
            throw new BadRequestException("账号已存在");
        }
        User user = User.builder()
                .account(request.account())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .phone(request.phone())
                .isStudent(request.isStudent())
                .school(request.school())
                .studentNumber(request.studentNumber())
                .gender(request.gender())
                .nickname(request.username())
                .avatar("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200' viewBox='0 0 200 200'%3E%3Crect width='200' height='200' fill='%23CCCCCC'/%3E%3Ctext x='50%25' y='50%25' font-size='100' text-anchor='middle' dominant-baseline='middle' fill='%23999999'%3E👤%3C/text%3E%3C/svg%3E")
                .isAdmin(false)
                .build();
        user.getRoles().add("ROLE_USER");
        User saved = userRepository.save(user);
        String token = "mock-token-" + UUID.randomUUID();
        return new AuthResponse(token, toDto(saved));
    }

    @Transactional(readOnly = true)
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        return toDto(findById(id));
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = findById(id);
        log.debug("更新用户信息: userId={}, avatar={}", id, request.avatar());
        
        if (request.username() != null) user.setUsername(request.username());
        // 移除邮箱修改功能
        // 移除手机号修改功能
        if (request.isStudent() != null) user.setIsStudent(request.isStudent());
        if (request.school() != null) user.setSchool(request.school());
        if (request.studentNumber() != null) user.setStudentNumber(request.studentNumber());
        if (request.nickname() != null) user.setNickname(request.nickname());
        
        // 处理头像更新 - 只要request.avatar()不为null就更新
        if (request.avatar() != null) {
            if (!request.avatar().isBlank()) {
                user.setAvatar(request.avatar());
                log.debug("更新用户头像: userId={}, avatar={}", id, request.avatar());
            } else {
                // 如果传入空字符串，清除头像
                user.setAvatar(null);
                log.debug("清除用户头像: userId={}", id);
            }
        }
        
        // 移除个人简介修改功能
        if (request.isAdmin() != null) user.setIsAdmin(request.isAdmin());
        // 添加密码修改功能
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        
        // 保存到数据库
        User saved = userRepository.save(user);
        // 确保立即刷新到数据库
        userRepository.flush();
        log.debug("用户信息已保存: userId={}, avatar={}", id, saved.getAvatar());
        
        return toDto(saved);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listUsers();
        }
        String trimmed = keyword.trim();
        return userRepository.findAll().stream()
                .filter(user ->
                        (user.getUsername() != null && user.getUsername().contains(trimmed)) ||
                        (user.getAccount() != null && user.getAccount().contains(trimmed)) ||
                        (user.getNickname() != null && user.getNickname().contains(trimmed)) ||
                        (user.getSchool() != null && user.getSchool().contains(trimmed)))
                .map(this::toDto)
                .toList();
    }
    
    public void resetPassword(String account, String phone, String newPassword) {
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new ResourceNotFoundException("账号不存在"));
        
        // 验证手机号是否匹配
        if (user.getPhone() == null) {
            throw new BadRequestException("该账号未设置手机号，无法重置密码");
        }
        
        if (!user.getPhone().equals(phone)) {
            throw new BadRequestException("手机号不匹配");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }

    private UserDto toDto(User user) {
        String avatar = user.getAvatar();
        return new UserDto(
                user.getId(),
                user.getAccount(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getIsStudent(),
                user.getSchool(),
                user.getStudentNumber(),
                user.getNickname(),
                avatar,
                user.getBio(),
                user.getGender(),
                user.getIsAdmin()
        );
    }
}

