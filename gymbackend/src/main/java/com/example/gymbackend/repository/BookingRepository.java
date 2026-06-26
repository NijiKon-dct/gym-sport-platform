package com.example.gymbackend.repository;

import com.example.gymbackend.model.Booking;
import com.example.gymbackend.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByVenueId(Long venueId);

    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByPaidFalseAndStatusNot(BookingStatus status);

    List<Booking> findByPaidFalseAndStatus(BookingStatus status);

    /** 重叠：半开区间，首尾相接（如 08:00-09:00 与 09:00-11:30）不计入冲突 */
    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.venue.id = :venueId " +
            "AND b.date = :date " +
            "AND b.status <> com.example.gymbackend.model.BookingStatus.CANCELLED " +
            "AND b.status <> com.example.gymbackend.model.BookingStatus.USER_CANCELLED " +
            "AND (b.startTime < :endTime AND b.endTime > :startTime)")
    long countOverlappingBookings(@Param("venueId") Long venueId,
                                  @Param("date") LocalDate date,
                                  @Param("startTime") java.time.LocalTime startTime,
                                  @Param("endTime") java.time.LocalTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.date BETWEEN :start AND :end")
    List<Booking> findByDateRange(@Param("start") LocalDate start,
                                  @Param("end") LocalDate end);

    @Query("SELECT b FROM Booking b WHERE b.venue.id = :venueId AND b.date = :date "
            + "AND b.status <> com.example.gymbackend.model.BookingStatus.CANCELLED "
            + "AND b.status <> com.example.gymbackend.model.BookingStatus.USER_CANCELLED "
            + "ORDER BY b.startTime ASC")
    List<Booking> findNonCancelledByVenueIdAndDate(@Param("venueId") Long venueId,
                                                   @Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.venue.id = :venueId AND b.date = CURRENT_DATE AND " +
           "FUNCTION('CURRENT_TIME') BETWEEN b.startTime AND b.endTime AND b.status IN (:statuses)")
    List<Booking> findCurrentBookingsByVenueId(@Param("venueId") Long venueId,
                                               @Param("statuses") List<BookingStatus> statuses);

    /**
     * 统计所有已确认预约的平均确认时长（单位：秒）
     * 使用 created_at 与 updated_at 的差值作为确认用时
     */
    @Query(
            value = "SELECT AVG(TIMESTAMPDIFF(SECOND, created_at, updated_at)) " +
                    "FROM bookings WHERE status = 'CONFIRMED'",
            nativeQuery = true
    )
    Double findAverageConfirmSeconds();
}

