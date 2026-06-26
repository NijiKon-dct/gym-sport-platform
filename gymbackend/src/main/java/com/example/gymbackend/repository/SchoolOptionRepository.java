package com.example.gymbackend.repository;

import com.example.gymbackend.model.SchoolOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolOptionRepository extends JpaRepository<SchoolOption, Long> {

    List<SchoolOption> findAllByEnabledIsTrueOrderBySortOrderDescNameAsc();

    List<SchoolOption> findAllByOrderBySortOrderDescNameAsc();

    Optional<SchoolOption> findByName(String name);
}

