package com.example.gymbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class FileConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 配置静态资源访问路径，用于访问上传的图片
        // 使用与FileUploadService相同的路径逻辑
        String userDir = System.getProperty("user.dir");
        Path basePath;
        
        if (userDir.endsWith("gymbackend") || userDir.contains("gymbackend")) {
            basePath = Paths.get(userDir).getParent();
        } else {
            basePath = Paths.get(userDir);
        }
        
        String uploadPath = basePath.resolve("save").toAbsolutePath().toString().replace("\\", "/");
        log.info("静态资源访问路径: {}", uploadPath);
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}

