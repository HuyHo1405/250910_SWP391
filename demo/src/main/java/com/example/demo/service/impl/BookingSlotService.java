package com.example.demo.service.impl;

import com.example.demo.model.dto.DailyBookedSlot;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.repo.BookingRepo;
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
public class BookingSlotService {

    private final BookingRepo bookingRepository;

    private static final int WORKING_HOUR_START = 7;
    private static final int WORKING_HOUR_END = 17;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        // 6. Nhóm (group) các LocalDateTime thành Map<String, List<Integer>>
        // (Kết quả: {"2025-11-05": [9, 11, 14], "2025-11-06": [7, 17]})
        Map<String, List<Integer>> slotsByDateString = bookedDateTimes.stream()
                .collect(Collectors.groupingBy(
                        ldt -> ldt.format(DATE_FORMATTER), // Nhóm theo "yyyy-MM-dd"
                        Collectors.mapping(LocalDateTime::getHour, Collectors.toList()) // Lấy giờ
                ));

        // 7. Xây dựng DTO Response (đảm bảo đủ số ngày, kể cả ngày trống)
        List<DailyBookedSlot> dailySlotsList = new ArrayList<>();

        for (int i = 0; i < daysToCheck; i++) {
            String dateKey = startDate.plusDays(i).format(DATE_FORMATTER);

            // Lấy list giờ đã book (nếu có), nếu không (chưa ai book) thì lấy list rỗng
            List<Integer> bookedHours = slotsByDateString.getOrDefault(dateKey, new ArrayList<>());

            // Sắp xếp giờ cho đẹp (ví dụ: [11, 7, 9] -> [7, 9, 11])
            bookedHours.sort(Integer::compareTo);

            dailySlotsList.add(new DailyBookedSlot(dateKey, bookedHours));
        }

        return dailySlotsList;
    }
}
