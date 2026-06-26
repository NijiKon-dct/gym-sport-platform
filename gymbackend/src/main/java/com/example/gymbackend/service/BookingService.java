package com.example.gymbackend.service;

import com.example.gymbackend.dto.booking.BookingRequest;
import com.example.gymbackend.dto.booking.BookingResponse;
import com.example.gymbackend.dto.booking.OccupiedSlotResponse;
import com.example.gymbackend.exception.BadRequestException;
import com.example.gymbackend.exception.ResourceNotFoundException;
import com.example.gymbackend.model.Booking;
import com.example.gymbackend.model.BookingStatus;
import com.example.gymbackend.model.User;
import com.example.gymbackend.model.Venue;
import com.example.gymbackend.repository.BookingRepository;
import com.example.gymbackend.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BookingService {
    private static final String AUTO_CANCEL_REASON = "系统自动取消：预约时间已结束且未完成付款";

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final VenueRepository venueRepository;

    public BookingResponse create(BookingRequest request) {
        User user = userService.findById(request.userId());
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("场馆不存在"));

        validateTimeRange(request.startTime(), request.endTime());
        ensureNotOverlapping(request.venueId(), request.date(), request.startTime(), request.endTime());

        double price = venue.getPrice() != null ? venue.getPrice() : 0d;
        Booking booking = Booking.builder()
                .user(user)
                .venue(venue)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(BookingStatus.PENDING)
                .price(price)
                .paid(false)
                .remark(request.remark())
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listAll() {
        return bookingRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public BookingResponse cancel(Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.USER_CANCELLED) {
            throw new BadRequestException("预约已取消");
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED && booking.isPaid()) {
            booking.setStatus(BookingStatus.USER_CANCELLED);
            booking.setPaid(false);
            booking.setRemark(appendRemark(booking.getRemark(), "用户取消预约（已退款）"));
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
        }
        return toResponse(booking);
    }

    private static String appendRemark(String existing, String note) {
        if (existing == null || existing.isBlank()) {
            return note;
        }
        if (existing.contains(note)) {
            return existing;
        }
        return existing + "；" + note;
    }

    public BookingResponse confirm(Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.USER_CANCELLED) {
            throw new BadRequestException("已取消的预约不能确认");
        }
        if (!booking.isPaid()) {
            throw new BadRequestException("请先完成付款再确认预约");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return toResponse(booking);
    }

    public BookingResponse pay(Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.isPaid()) {
            return toResponse(booking);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.USER_CANCELLED) {
            if (AUTO_CANCEL_REASON.equals(booking.getRemark())) {
                throw new BadRequestException("预约已因未付款且超时被自动取消，无法付款");
            }
            throw new BadRequestException("已取消的预约不能付款");
        }
        // 允许在待确认或已确认状态付款
        booking.setPaid(true);
        return toResponse(booking);
    }

    @Scheduled(cron = "${booking.auto-cancel.cron:0 */1 * * * *}")
    public void autoCancelExpiredUnpaidBookings() {
        LocalDateTime now = LocalDateTime.now();
        // 仅处理「待确认且未付款」超时单，避免误伤用户主动取消（已退款）的记录
        List<Booking> unpaidBookings = bookingRepository.findByPaidFalseAndStatus(BookingStatus.PENDING);
        int cancelledCount = 0;

        for (Booking booking : unpaidBookings) {
            LocalDateTime bookingEnd = LocalDateTime.of(booking.getDate(), booking.getEndTime());
            if (bookingEnd.isBefore(now)) {
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setRemark(AUTO_CANCEL_REASON);
                cancelledCount++;
            }
        }

        if (cancelledCount > 0) {
            log.info("自动取消未付款超时预约数量: {}", cancelledCount);
        }
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listByDateRange(LocalDate start, LocalDate end) {
        return bookingRepository.findByDateRange(start, end).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 某日该场馆已被占用的时段（不含已取消），用于预约页展示。
     */
    @Transactional(readOnly = true)
    public List<OccupiedSlotResponse> listOccupiedSlots(Long venueId, LocalDate date) {
        if (!venueRepository.existsById(venueId)) {
            throw new ResourceNotFoundException("场馆不存在");
        }
        return bookingRepository.findNonCancelledByVenueIdAndDate(venueId, date).stream()
                .map(b -> new OccupiedSlotResponse(b.getStartTime(), b.getEndTime()))
                .toList();
    }

    /**
     * 获取所有已确认预约的平均确认用时（单位：秒）
     * 计算方式：所有状态为 CONFIRMED 的预约，updatedAt - createdAt 的平均值
     */
    @Transactional(readOnly = true)
    public long getAverageConfirmSeconds() {
        Double avg = bookingRepository.findAverageConfirmSeconds();
        if (avg == null) {
            return 0L;
        }
        // 四舍五入到最近的秒
        return Math.round(avg);
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("预约不存在"));
    }

    private void ensureNotOverlapping(Long venueId, LocalDate date, LocalTime start, LocalTime end) {
        long count = bookingRepository.countOverlappingBookings(venueId, date, start, end);
        if (count > 0) {
            throw new BadRequestException("您选择的时间段与已有预约重叠，该时段已被他人预约，请更换时间");
        }
    }

    private void validateTimeRange(LocalTime start, LocalTime end) {
        if (!end.isAfter(start)) {
            throw new BadRequestException("结束时间必须晚于开始时间");
        }
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getVenue().getId(),
                booking.getVenue().getName(),
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus().name().toLowerCase(),
                booking.getPrice(),
                booking.isPaid(),
                booking.getRemark(),
                booking.getCreatedAt()
        );
    }
}

