package com.example.demo.service.impl;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.ReportResponse;
import com.example.demo.model.entity.Job;
import com.example.demo.model.entity.Part;
import com.example.demo.model.entity.User;
import com.example.demo.model.modelEnum.AlertType;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.repo.*;
import com.example.demo.service.interfaces.IReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService implements IReportService {

    private final AccessControlService accessControlService;

    private final PaymentRepo paymentRepository;
    private final PartRepo partRepository;
    private final UserRepo userRepository;
    private final BookingRepo bookingRepository;
    private final JobRepo jobRepository;

    private static final BigDecimal LOW_STOCK_THRESHOLD = BigDecimal.valueOf(50);

    @Override
    public ReportResponse.DashboardRevenue getReportDashboardRevenue() {

        accessControlService.verifyResourceAccessWithoutOwnership("DASHBOARD", "VIEW");

        // 1. Xác định kỳ hiện tại và kỳ trước (ví dụ: theo tháng)
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        // 2. Tính toán ngày bắt đầu/kết thúc cho cả hai kỳ
        // Kỳ hiện tại (ví dụ: 01/11/2025 00:00:00 -> 01/12/2025 00:00:00)
        LocalDateTime currentMonthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime currentMonthEnd = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        // Kỳ trước (ví dụ: 01/10/2025 00:00:00 -> 01/11/2025 00:00:00)
        LocalDateTime previousMonthStart = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime previousMonthEnd = currentMonthStart; // Kết thúc của tháng trước = bắt đầu của tháng này

        // 3. Truy vấn database để lấy doanh thu
        // (Dùng Optional... để xử lý trường hợp SUM trả về NULL)
        BigDecimal currentRevenue = Optional.ofNullable(
                paymentRepository.sumSuccessfulRevenueBetween(currentMonthStart, currentMonthEnd)
        ).orElse(BigDecimal.ZERO);

        BigDecimal previousRevenue = Optional.ofNullable(
                paymentRepository.sumSuccessfulRevenueBetween(previousMonthStart, previousMonthEnd)
        ).orElse(BigDecimal.ZERO);

        // 4. Tính toán phần trăm thay đổi
        BigDecimal percentageChange;

        // Xử lý trường hợp chia cho 0
        if (previousRevenue.compareTo(BigDecimal.ZERO) == 0) {
            if (currentRevenue.compareTo(BigDecimal.ZERO) == 0) {
                percentageChange = BigDecimal.ZERO; // 0 vs 0 -> 0%
            } else {
                percentageChange = new BigDecimal("100.00"); // 0 vs >0 -> tăng 100%
            }
        } else {
            // Công thức: ((hiện tại - trước) / trước) * 100
            BigDecimal change = currentRevenue.subtract(previousRevenue);
            percentageChange = change.multiply(new BigDecimal("100"))
                    .divide(previousRevenue, 2, RoundingMode.HALF_UP);
        }

        // 5. Tạo đối tượng Response
        String periodName = String.format("Tháng %d/%d", currentMonth.getMonthValue(), currentMonth.getYear());

        return ReportResponse.DashboardRevenue.builder()
                .period(periodName)
                .totalRevenue(currentRevenue)
                .percentageChangeVsPreviousPeriod(percentageChange)
                .build();
    }

    @Override
    public ReportResponse.DashboardActiveUserCount getReportDashboardUserCounts() {

        accessControlService.verifyResourceAccessWithoutOwnership("DASHBOARD", "VIEW");

        // 1. Đếm tổng số user "truy cập" (đang ACTIVE)
        long activeCustomer = userRepository.countByRole_DisplayNameAndStatus("Khách hàng", EntityStatus.ACTIVE);
        // 2. Đếm số "nhân sự" (không phải Customer) đang ACTIVE
        long activeEmployee = userRepository.countByRole_DisplayNameNotAndStatus("Khách hàng", EntityStatus.ACTIVE);

        return ReportResponse.DashboardActiveUserCount.builder()
                .totalActiveCustomer(activeCustomer)
                .totalActiveEmployee(activeEmployee)
                .build();
    }

    @Override
    public ReportResponse.DashboardActiveBookingCount getReportDashboardBookingCounts() {

        accessControlService.verifyResourceAccessWithoutOwnership("DASHBOARD", "VIEW");

        long completeBooking = bookingRepository.countByBookingStatus(BookingStatus.MAINTENANCE_COMPLETE);
        long notCompleteBooking = bookingRepository.countByBookingStatusNot(BookingStatus.MAINTENANCE_COMPLETE);
        return ReportResponse.DashboardActiveBookingCount.builder()
                .totalCompleteBooking(completeBooking)
                .totalNotCompleteBooking(notCompleteBooking)
                .build();
    }

    @Override
    public ReportResponse.DashboardAlertCount getReportDashboardAlertCounts() {

        accessControlService.verifyResourceAccessWithoutOwnership("DASHBOARD", "VIEW");

        // Hiện tại chưa có entity Alert, tạm thời trả về 0
        long totalAlert = 0L;

        List<Part> lowStock = partRepository.findByQuantityLessThanEqual(LOW_STOCK_THRESHOLD);

        if(!lowStock.isEmpty()){
            totalAlert += lowStock.size();
        }

        return ReportResponse.DashboardAlertCount.builder()
                .totalAlert(totalAlert)
                .build();
    }

    @Override
    public List<ReportResponse.Alerts> getAlerts() {
        accessControlService.verifyResourceAccessWithoutOwnership("DASHBOARD", "VIEW");

        List<Part> lowStock = partRepository.findByQuantityLessThanEqual(LOW_STOCK_THRESHOLD);

        return lowStock.stream()
                .map(this::createLowStockAlert)
                .toList();
    }

    @Override
    public List<ReportResponse.TopPerformance> getTopPerformance() {

        accessControlService.verifyResourceAccessWithoutOwnership("DASHBOARD", "VIEW");

        // 1. Lấy dữ liệu
        List<Job> completedJobs = jobRepository.findByTechnicianIsNotNullAndActualEndTimeIsNotNullAndEstEndTimeIsNotNull();

        // 2. Nhóm theo kỹ thuật viên
        Map<User, List<Job>> jobsByTechnician = completedJobs.stream()
                .collect(Collectors.groupingBy(Job::getTechnician));

        // 3. Xử lý và tính toán
        List<ReportResponse.TopPerformance> performanceList = jobsByTechnician.entrySet().stream()
                .map(entry -> {
                    User technician = entry.getKey();
                    List<Job> techJobs = entry.getValue();

                    // --- LOGIC MỚI ---

                    // Đếm tổng số công việc
                    long totalJobs = techJobs.size();

                    // Tính tổng thời gian dự kiến (giây)
                    long totalEstDurationSeconds = techJobs.stream()
                            .mapToLong(job ->
                                    Duration.between(job.getStartTime(), job.getEstEndTime()).toSeconds())
                            .sum();

                    // Tính tổng thời gian thực tế (giây)
                    long totalActualDurationSeconds = techJobs.stream()
                            .mapToLong(job ->
                                    Duration.between(job.getStartTime(), job.getActualEndTime()).toSeconds())
                            .sum();

                    // Tính điểm hiệu suất: (Dự kiến / Thực tế) * 100
                    // Điểm càng cao càng tốt
                    double performancePercentage = 0.0;
                    if (totalActualDurationSeconds > 0) { // Tránh chia cho 0
                        performancePercentage = ((double) totalEstDurationSeconds / totalActualDurationSeconds) * 100.0;
                    } else if (totalEstDurationSeconds == 0) {
                        // Nếu cả hai đều = 0, coi như 100%
                        performancePercentage = 100.0;
                    }
                    // (Nếu Thực tế = 0 nhưng Dự kiến > 0, điểm sẽ là Vô hạn, nhưng
                    // trường hợp này không xảy ra vì ta đã lọc các job hoàn thành)

                    // Tạo DTO với thông tin mới
                    return ReportResponse.TopPerformance.builder()
                            .technicianId(technician.getId())
                            .technicianName(technician.getFullName())
                            .performanceScorePercentage(performancePercentage) // Điểm mới
                            .completedJobCount(totalJobs) // Thêm tổng số job
                            .build();
                })
                .collect(Collectors.toList());

        // 4. SẮP XẾP GIẢM DẦN (DESC)
        // Điểm % cao nhất (tốt nhất) sẽ ở trên cùng
        performanceList.sort((p1, p2) -> p2.getPerformanceScorePercentage().compareTo(p1.getPerformanceScorePercentage()));

        // 5. Trả top 3 về kết quả
        return performanceList.subList(0, 3);
    }

    /**
     * Lấy báo cáo doanh thu 6 tháng gần nhất (gọi 6 câu query).
     * @return Danh sách dữ liệu doanh thu của 6 tháng.
     */
    @Override
    public List<ReportResponse.MonthlyRevenueData> getRevenueReportLast6Months_Loop() {

        accessControlService.verifyResourceAccessWithoutOwnership("DASHBOARD", "VIEW");

        List<ReportResponse.MonthlyRevenueData> finalReport = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        // Lặp 6 lần, bắt đầu từ 5 tháng trước
        // i=5 (5 tháng trước), i=4, i=3, i=2, i=1, i=0 (tháng này)
        for (int i = 5; i >= 0; i--) {

            // 1. Xác định tháng cần tính
            YearMonth monthToCalculate = currentMonth.minusMonths(i);

            // 2. Tính toán ngày bắt đầu/kết thúc cho tháng đó
            LocalDateTime startDate = monthToCalculate.atDay(1).atStartOfDay();
            LocalDateTime endDate = monthToCalculate.plusMonths(1).atDay(1).atStartOfDay();

            // 3. Gọi query (đây là 1 trong 6 lần gọi)
            BigDecimal revenue = Optional.ofNullable(
                    paymentRepository.sumSuccessfulRevenueBetween(startDate, endDate)
            ).orElse(BigDecimal.ZERO);

            // 4. Tạo tên hiển thị
            String periodName = String.format("Tháng %d/%d", monthToCalculate.getMonthValue(), monthToCalculate.getYear());

            // 5. Thêm vào danh sách
            finalReport.add(ReportResponse.MonthlyRevenueData.builder()
                    .period(periodName)
                    .totalRevenue(revenue)
                    .build());
        }

        // 6. Trả về danh sách
        return finalReport;
    }


    private ReportResponse.Alerts createLowStockAlert(Part part) {
        return ReportResponse.Alerts.builder()
                .alertMessage("Phụ tùng " + part.getName() + " có số lượng thấp: " + part.getAvailableQuantity())
                .alertType(AlertType.WARNING)
                .build();
    }

}
