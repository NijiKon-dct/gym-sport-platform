package com.example.gymbackend.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VenueFavoriteId implements Serializable {
    private Long userId;
    private Long venueId;
}















