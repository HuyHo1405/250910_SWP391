package com.example.demo.service.impl;

import com.example.demo.model.dto.DailyBookedSlot;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.service.interfaces.IBookingSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự động inject BookingRepo
@Slf4j
public class BookingSlotService implements IBookingSlotService {

    private final BookingRepo bookingRepository;

    private static final int WORKING_HOUR_START = 7;
    private static final int WORKING_HOUR_END = 17;
    private static final int MAX_BOOKING_PER_SLOT = 5; // Số booking tối đa cho 1 slot
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public List<DailyBookedSlot> getBookedSlot(){
        LocalDate startDate = LocalDate.now();
        int daysToCheck = 7;

        log.info("Đang lấy các slot đã đặt từ {} ({} ngày), khung giờ {}-{}",
                startDate, daysToCheck, WORKING_HOUR_START, WORKING_HOUR_END);

        // 3. Xác định khung thời gian tổng (ví dụ: 7 ngày)
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = startDate.plusDays(daysToCheck).atStartOfDay(); // 00:00 của ngày thứ N+1

        // 4. Các trạng thái "đang chiếm chỗ"
        List<BookingStatus> activeStatuses = Arrays.asList(
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED,
                BookingStatus.IN_PROGRESS,
                BookingStatus.MAINTENANCE_COMPLETE
        );

        // 5. Gọi Repo (Giả định bạn đã thêm hàm này vào BookingRepo)
        List<LocalDateTime> bookedDateTimes = bookingRepository
                .findBookedDateTimesInWorkingHours(
                        startTime,
                        endTime,
                        activeStatuses,
                        WORKING_HOUR_START,
                        WORKING_HOUR_END
                );

        // 6. Nhóm (group) các LocalDateTime thành Map<String, Map<Integer, Long>>
        // Đếm số lượng booking cho mỗi cặp (ngày, giờ)
        // VD: {"2025-11-05": {9: 3, 11: 2, 14: 1}, "2025-11-06": {7: 1, 17: 2}}
        Map<String, Map<Integer, Long>> slotCountsByDate = bookedDateTimes.stream()
                .collect(Collectors.groupingBy(
                        ldt -> ldt.format(DATE_FORMATTER), // Nhóm theo "yyyy-MM-dd"
                        Collectors.groupingBy(
                                LocalDateTime::getHour,    // Nhóm theo giờ
                                Collectors.counting()       // Đếm số lượng
                        )
                ));

        // 7. Xây dựng DTO Response (đảm bảo đủ số ngày, kể cả ngày trống)
        List<DailyBookedSlot> dailySlotsList = new ArrayList<>();

        for (int i = 0; i < daysToCheck; i++) {
            String dateKey = startDate.plusDays(i).format(DATE_FORMATTER);
            Map<Integer, Long> hourCounts = slotCountsByDate.getOrDefault(dateKey, Map.of());

            // Chỉ lấy các giờ đã full slot
            List<Integer> bookedHours = hourCounts.entrySet().stream()
                .filter(e -> e.getValue() >= MAX_BOOKING_PER_SLOT)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

            dailySlotsList.add(new DailyBookedSlot(dateKey, bookedHours));
        }

        return dailySlotsList;
    }
}