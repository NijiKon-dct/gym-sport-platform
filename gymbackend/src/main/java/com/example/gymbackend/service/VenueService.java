package com.example.gymbackend.service;

import com.example.gymbackend.dto.venue.VenueRequest;
import com.example.gymbackend.dto.venue.VenueResponse;
import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.Venue;
import com.example.gymbackend.model.BookingStatus;
import com.example.gymbackend.repository.BookingRepository;
import com.example.gymbackend.repository.VenueRepository;
import com.example.gymbackend.repository.VenueReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VenueService {

    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final VenueInteractionService interactionService;
    private final VenueReviewRepository venueReviewRepository;

    @Transactional(readOnly = true)
    public List<VenueResponse> findAll(Long userId) {
        return venueRepository.findAll().stream()
                .map(venue -> toResponse(venue, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> findFavoriteVenues(Long userId) {
        return venueRepository.findAll().stream()
                .filter(venue -> interactionService.isFavorite(userId, venue.getId()))
                .map(venue -> toResponse(venue, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public VenueResponse findById(Long id, Long userId) {
        return toResponse(getEntity(id), userId);
    }

    public VenueResponse create(VenueRequest request) {
        Venue venue = Venue.builder()
                .name(request.name())
                .address(request.address())
                .type(request.type())
                .capacity(request.capacity())
                .price(request.price())
                .image(request.image())
                .description(request.description())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .school(request.school())
                .build();
        return toResponse(venueRepository.save(venue), null);
    }

    public VenueResponse update(Long id, VenueRequest request) {
        Venue venue = getEntity(id);
        venue.setName(request.name());
        venue.setAddress(request.address());
        venue.setType(request.type());
        venue.setCapacity(request.capacity());
        venue.setPrice(request.price());
        venue.setImage(request.image());
        venue.setDescription(request.description());
        venue.setOpenTime(request.openTime());
        venue.setCloseTime(request.closeTime());
        venue.setSchool(request.school());
        return toResponse(venue, null);
    }

    public void delete(Long id) {
        venueRepository.delete(getEntity(id));
    }

    private Venue getEntity(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("场馆不存在"));
    }

    private VenueResponse toResponse(Venue venue, Long userId) {
        // 检查场馆当前是否正在使用中（当前时间有有效的预约）
        boolean isInUse = !bookingRepository.findCurrentBookingsByVenueId(
                venue.getId(),
                Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED)
        ).isEmpty();
        
        // 查询用户是否收藏
        Boolean isFavorite = userId != null ? interactionService.isFavorite(userId, venue.getId()) : null;
        
        // 查询评分信息
        Double averageRating = venueReviewRepository.findAverageRatingByVenueId(venue.getId());
        if (averageRating == null) {
            averageRating = 0.0;
        }
        Long reviewCount = venueReviewRepository.countByVenueId(venue.getId());
        
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getAddress(),
                venue.getType(),
                venue.getCapacity(),
                venue.getPrice(),
                venue.getImage(),
                venue.getDescription(),
                venue.getOpenTime(),
                venue.getCloseTime(),
                venue.getSchool(),
                isInUse,
                isFavorite,
                averageRating,
                reviewCount
        );
    }
}

