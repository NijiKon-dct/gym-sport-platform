package com.example.gymbackend.repository;

import com.example.gymbackend.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findBySchool(String school);
}

