package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.AlertType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class ReportResponse {
    @Data
    @Builder
    public static class DashboardRevenue {

        private String period;

        private BigDecimal totalRevenue;

        private BigDecimal percentageChangeVsPreviousPeriod;

    }

    @Data
    @Builder
    public static class DashboardActiveUserCount {
        private long totalActiveCustomer;// Tổng số user đang hoạt động
        private long totalActiveEmployee; // Tổng số nhân sự đang hoạt động
    }

    @Data
    @Builder
    public static class DashboardActiveBookingCount {
        private long totalCompleteBooking;// Tổng số user đang hoạt động
        private long totalNotCompleteBooking; // Tổng số nhân sự đang hoạt động
    }

    @Data
    @Builder
    public static class DashboardAlertCount {
        private long totalAlert; // Tổng số cảnh báo hiện tại
    }

    @Data
    @Builder
    public static class Alerts {
        private String alertMessage;
        private AlertType alertType;
    }

    @Data
    @Builder
    public static class TopPerformance {
        private Long technicianId;
        private String technicianName;
        private Long completedJobCount;
        private Double performanceScorePercentage;
    }

    @Data
    @Builder
    public static class MonthlyRevenueData {
        private String period; // Ví dụ: "Tháng 11/2025"
        private BigDecimal totalRevenue;
    }
}
