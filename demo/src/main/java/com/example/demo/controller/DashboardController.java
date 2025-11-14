package com.example.demo.controller;

import com.example.demo.model.dto.ReportResponse;
import com.example.demo.service.interfaces.IPartService;
import com.example.demo.service.interfaces.IReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {
    private final IReportService reportService;

    @GetMapping("/revenue")
    public ResponseEntity<ReportResponse.DashboardRevenue> getDashboardRevenue() {
        ReportResponse.DashboardRevenue response = reportService.getReportDashboardRevenue();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-counts")
    public ResponseEntity<ReportResponse.DashboardActiveUserCount> getDashboardUserCounts() {
        ReportResponse.DashboardActiveUserCount response = reportService.getReportDashboardUserCounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking-counts")
    public ResponseEntity<ReportResponse.DashboardActiveBookingCount> getDashboardBookingCounts() {
        ReportResponse.DashboardActiveBookingCount response = reportService.getReportDashboardBookingCounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alert-counts")
    public ResponseEntity<ReportResponse.DashboardAlertCount> getDashboardAlertCounts() {
        ReportResponse.DashboardAlertCount response = reportService.getReportDashboardAlertCounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alert")
    public ResponseEntity<List<ReportResponse.Alerts>> getAlerts() {
        List<ReportResponse.Alerts> response = reportService.getAlerts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-performance")
    public ResponseEntity<List<?>> getTopPerformance() {
        List<?> response = reportService.getTopPerformance();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/charts/revenue-last-6-months")
    public ResponseEntity<List<ReportResponse.MonthlyRevenueData>> getRevenueChartLast6Months() {
        List<ReportResponse.MonthlyRevenueData> data = reportService.getRevenueReportLast6Months_Loop();
        return ResponseEntity.ok(data);
    }

}
