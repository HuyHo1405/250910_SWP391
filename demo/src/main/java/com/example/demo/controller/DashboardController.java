package com.example.demo.controller;

import com.example.demo.model.dto.ReportResponse;
import com.example.demo.service.interfaces.IReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for dashboard statistics and reports - Admin")
public class DashboardController {
    private final IReportService reportService;

    @GetMapping("/revenue")
    @Operation(
        summary = "Get dashboard revenue",
        description = "Returns revenue statistics for the dashboard. Requires admin authentication."
    )
    public ResponseEntity<ReportResponse.DashboardRevenue> getDashboardRevenue() {
        ReportResponse.DashboardRevenue response = reportService.getReportDashboardRevenue();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-counts")
    @Operation(
        summary = "Get dashboard user counts",
        description = "Returns active user counts for the dashboard. Requires admin authentication."
    )
    public ResponseEntity<ReportResponse.DashboardActiveUserCount> getDashboardUserCounts() {
        ReportResponse.DashboardActiveUserCount response = reportService.getReportDashboardUserCounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking-counts")
    @Operation(
        summary = "Get dashboard booking counts",
        description = "Returns active booking counts for the dashboard. Requires admin authentication."
    )
    public ResponseEntity<ReportResponse.DashboardActiveBookingCount> getDashboardBookingCounts() {
        ReportResponse.DashboardActiveBookingCount response = reportService.getReportDashboardBookingCounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alert-counts")
    @Operation(
        summary = "Get dashboard alert counts",
        description = "Returns alert counts for the dashboard. Requires admin authentication."
    )
    public ResponseEntity<ReportResponse.DashboardAlertCount> getDashboardAlertCounts() {
        ReportResponse.DashboardAlertCount response = reportService.getReportDashboardAlertCounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alert")
    @Operation(
        summary = "Get dashboard alerts",
        description = "Returns alert details for the dashboard. Requires admin authentication."
    )
    public ResponseEntity<List<ReportResponse.Alerts>> getAlerts() {
        List<ReportResponse.Alerts> response = reportService.getAlerts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-performance")
    @Operation(
        summary = "Get dashboard top performance",
        description = "Returns top performance statistics for the dashboard. Requires admin authentication."
    )
    public ResponseEntity<List<?>> getTopPerformance() {
        List<?> response = reportService.getTopPerformance();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/charts/revenue-last-6-months")
    @Operation(
        summary = "Get revenue chart for last 6 months",
        description = "Returns monthly revenue data for the last 6 months for the dashboard. Requires admin authentication."
    )
    public ResponseEntity<List<ReportResponse.MonthlyRevenueData>> getRevenueChartLast6Months() {
        List<ReportResponse.MonthlyRevenueData> data = reportService.getRevenueReportLast6Months_Loop();
        return ResponseEntity.ok(data);
    }

}
