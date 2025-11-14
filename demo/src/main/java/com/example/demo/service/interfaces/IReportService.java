package com.example.demo.service.interfaces;

import com.example.demo.model.dto.ReportResponse;

import java.util.List;

public interface IReportService {
    ReportResponse.DashboardRevenue getReportDashboardRevenue();
    ReportResponse.DashboardActiveUserCount getReportDashboardUserCounts();
    ReportResponse.DashboardActiveBookingCount getReportDashboardBookingCounts();
    ReportResponse.DashboardAlertCount getReportDashboardAlertCounts();
    List<ReportResponse.Alerts> getAlerts();
    List<ReportResponse.TopPerformance> getTopPerformance();
    List<ReportResponse.MonthlyRevenueData> getRevenueReportLast6Months_Loop();
}
