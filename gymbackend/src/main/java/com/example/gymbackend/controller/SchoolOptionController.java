package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.school.CreateSchoolOptionRequest;
import com.example.gymbackend.dto.school.SchoolOptionDto;
import com.example.gymbackend.service.SchoolOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schools")
public class SchoolOptionController {

    private final SchoolOptionService schoolOptionService;

    /**
     * 注册页使用：返回启用中的学校名称列表
     */
    @GetMapping("/options")
    public ApiResponse<List<String>> listEnabledNames() {
        List<String> names = schoolOptionService.listAllEnabled()
                .stream()
                .map(SchoolOptionDto::name)
                .toList();
        return ApiResponse.ok(names);
    }

    /**
     * 管理端使用：返回所有学校选项详情
     */
    @GetMapping
    public ApiResponse<List<SchoolOptionDto>> listAll() {
        return ApiResponse.ok(schoolOptionService.listAll());
    }

    @PostMapping
    public ApiResponse<SchoolOptionDto> create(@Valid @RequestBody CreateSchoolOptionRequest request) {
        return ApiResponse.ok(schoolOptionService.create(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        schoolOptionService.delete(id);
        return ApiResponse.ok(null);
    }
}

