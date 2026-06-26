package com.example.gymbackend.service;

import com.example.gymbackend.dto.school.CreateSchoolOptionRequest;
import com.example.gymbackend.dto.school.SchoolOptionDto;
import com.example.gymbackend.model.SchoolOption;
import com.example.gymbackend.repository.SchoolOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolOptionService {

    private final SchoolOptionRepository schoolOptionRepository;

    @Transactional(readOnly = true)
    public List<SchoolOptionDto> listAllEnabled() {
        return schoolOptionRepository.findAllByEnabledIsTrueOrderBySortOrderDescNameAsc()
                .stream()
                .map(SchoolOptionDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SchoolOptionDto> listAll() {
        return schoolOptionRepository.findAllByOrderBySortOrderDescNameAsc()
                .stream()
                .map(SchoolOptionDto::fromEntity)
                .toList();
    }

    @Transactional
    public SchoolOptionDto create(CreateSchoolOptionRequest request) {
        String name = request.name().trim();
        schoolOptionRepository.findByName(name).ifPresent(existing -> {
            throw new IllegalArgumentException("学校已存在");
        });
        SchoolOption entity = SchoolOption.builder()
                .name(name)
                .enabled(true)
                .sortOrder(0)
                .build();
        return SchoolOptionDto.fromEntity(schoolOptionRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        schoolOptionRepository.deleteById(id);
    }
}

