package com.example.gymbackend.dto.school;

import com.example.gymbackend.model.SchoolOption;

public record SchoolOptionDto(
        Long id,
        String name,
        Boolean enabled,
        Integer sortOrder
) {
    public static SchoolOptionDto fromEntity(SchoolOption entity) {
        return new SchoolOptionDto(
                entity.getId(),
                entity.getName(),
                entity.getEnabled(),
                entity.getSortOrder()
        );
    }
}

