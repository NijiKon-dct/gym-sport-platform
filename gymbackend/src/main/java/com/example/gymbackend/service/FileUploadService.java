package com.example.gymbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadService {

    // 基础保存路径 - 使用项目根目录下的save文件夹
    private static final String BASE_UPLOAD_DIR;
    
    // 各类型图片的保存目录
    private static final String PROFILE_PICTURE_DIR = "Profile Picture";
    private static final String VENUE_PICTURE_DIR = "Venue Pictures";
    private static final String SOCIAL_PICTURE_DIR = "APicture";
    private static final String CHAT_PICTURE_DIR = "Chat Pictures";
    private static final String CHAT_VIDEO_DIR = "Chat Video";
    private static final String SOCIAL_VIDEO_DIR = "AVideo";
    private static final String BOOKING_PICTURE_DIR = "BPicture";
    private static final String BOOKING_VIDEO_DIR = "BVideo";

    static {
        // 获取项目根目录（gym目录）
        String userDir = System.getProperty("user.dir");
        Path basePath;
        
        // 如果当前目录是gymbackend，则向上一级到gym目录
        if (userDir.endsWith("gymbackend") || userDir.contains("gymbackend")) {
            basePath = Paths.get(userDir).getParent();
        } else {
            // 否则在当前目录
            basePath = Paths.get(userDir);
        }
        
        BASE_UPLOAD_DIR = basePath.resolve("save").toAbsolutePath().toString();
        log.info("文件上传基础目录: {}", BASE_UPLOAD_DIR);
    }

    /**
     * 上传头像
     */
    public String uploadProfilePicture(MultipartFile file) throws IOException {
        return uploadFile(file, PROFILE_PICTURE_DIR, "image/");
    }

    /**
     * 上传场馆图片
     */
    public String uploadVenuePicture(MultipartFile file) throws IOException {
        return uploadFile(file, VENUE_PICTURE_DIR, "image/");
    }

    /**
     * 上传社交图片（支持多张）
     */
    public List<String> uploadSocialPictures(MultipartFile[] files) throws IOException {
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                paths.add(uploadFile(file, SOCIAL_PICTURE_DIR, "image/"));
            }
        }
        return paths;
    }

    /**
     * 上传单张聊天图片
     */
    public String uploadChatPicture(MultipartFile file) throws IOException {
        return uploadFile(file, CHAT_PICTURE_DIR, "image/");
    }

    /**
     * 上传单个聊天视频
     */
    public String uploadChatVideo(MultipartFile file) throws IOException {
        return uploadFile(file, CHAT_VIDEO_DIR, "video/");
    }

    /**
     * 上传社交动态视频
     */
    public String uploadSocialVideo(MultipartFile file) throws IOException {
        return uploadFile(file, SOCIAL_VIDEO_DIR, "video/");
    }

    /**
     * 上传场馆评论图片（晒单图片）
     */
    public String uploadBookingPicture(MultipartFile file) throws IOException {
        return uploadFile(file, BOOKING_PICTURE_DIR, "image/");
    }

    /**
     * 上传场馆评论视频（晒单视频）
     */
    public String uploadBookingVideo(MultipartFile file) throws IOException {
        return uploadFile(file, BOOKING_VIDEO_DIR, "video/");
    }

    /**
     * 上传多张场馆评论图片（晒单图片）
     */
    public List<String> uploadBookingPictures(MultipartFile[] files) throws IOException {
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                paths.add(uploadFile(file, BOOKING_PICTURE_DIR, "image/"));
            }
        }
        return paths;
    }

    /**
     * 通用文件上传方法
     */
    private String uploadFile(MultipartFile file, String subDir, String allowedPrefix) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(allowedPrefix)) {
            throw new IllegalArgumentException("文件类型不支持，仅允许上传 " + allowedPrefix + "*");
        }

        // 创建保存目录
        Path uploadPath = Paths.get(BASE_UPLOAD_DIR, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        log.debug("文件保存路径: {}", uploadPath.toAbsolutePath());

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.debug("文件已保存: path={}, size={} bytes", filePath.toAbsolutePath(), file.getSize());

        // 返回相对路径（用于存储在数据库中）
        String relativePath = Paths.get(subDir, filename).toString().replace("\\", "/");
        log.debug("返回的相对路径: {}", relativePath);
        return relativePath;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(BASE_UPLOAD_DIR, filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            // 记录日志但不抛出异常，避免影响主流程
            log.warn("删除文件失败: {}, 错误: {}", filePath, e.getMessage());
        }
    }

    /**
     * 获取文件的完整路径（用于访问）
     */
    public String getFullPath(String relativePath) {
        return Paths.get(BASE_UPLOAD_DIR, relativePath).toString().replace("\\", "/");
    }
}

