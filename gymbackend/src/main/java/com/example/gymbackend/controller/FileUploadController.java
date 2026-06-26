package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 上传头像
     */
    @PostMapping("/profile")
    public ApiResponse<Map<String, String>> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            String path = fileUploadService.uploadProfilePicture(file);
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", "/api/files/" + path);
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 上传场馆图片
     */
    @PostMapping("/venue")
    public ApiResponse<Map<String, String>> uploadVenuePicture(@RequestParam("file") MultipartFile file) {
        try {
            String path = fileUploadService.uploadVenuePicture(file);
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", "/api/files/" + path);
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 上传社交图片（支持多张）
     */
    @PostMapping("/social")
    public ApiResponse<Map<String, Object>> uploadSocialPictures(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> paths = fileUploadService.uploadSocialPictures(files);
            Map<String, Object> result = new HashMap<>();
            result.put("paths", paths);
            result.put("urls", paths.stream().map(path -> "/api/files/" + path).toList());
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 上传聊天图片
     */
    @PostMapping("/chat")
    public ApiResponse<Map<String, String>> uploadChatImage(@RequestParam("file") MultipartFile file) {
        try {
            String path = fileUploadService.uploadChatPicture(file);
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", "/api/files/" + path);
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 上传聊天视频
     */
    @PostMapping("/chat/video")
    public ApiResponse<Map<String, String>> uploadChatVideo(@RequestParam("file") MultipartFile file) {
        try {
            String path = fileUploadService.uploadChatVideo(file);
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", "/api/files/" + path);
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 上传社交动态视频
     */
    @PostMapping("/social/video")
    public ApiResponse<Map<String, String>> uploadSocialVideo(@RequestParam("file") MultipartFile file) {
        try {
            String path = fileUploadService.uploadSocialVideo(file);
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", "/api/files/" + path);
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 上传场馆评论图片（晒单图片）
     */
    @PostMapping("/booking/picture")
    public ApiResponse<Map<String, String>> uploadBookingPicture(@RequestParam("file") MultipartFile file) {
        try {
            String path = fileUploadService.uploadBookingPicture(file);
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", "/api/files/" + path);
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 上传多张场馆评论图片（晒单图片）
     */
    @PostMapping("/booking/pictures")
    public ApiResponse<Map<String, Object>> uploadBookingPictures(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> paths = fileUploadService.uploadBookingPictures(files);
            Map<String, Object> result = new HashMap<>();
            result.put("paths", paths);
            result.put("urls", paths.stream().map(path -> "/api/files/" + path).toList());
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 上传场馆评论视频（晒单视频）
     */
    @PostMapping("/booking/video")
    public ApiResponse<Map<String, String>> uploadBookingVideo(@RequestParam("file") MultipartFile file) {
        try {
            String path = fileUploadService.uploadBookingVideo(file);
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", "/api/files/" + path);
            return ApiResponse.ok(result);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}

