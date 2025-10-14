package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface IMaintenanceService {
    BookingResponse startInspection(Long bookingId);                // chuyển IDLE → INSPECTING
    BookingResponse requestApproval(Long bookingId);                // chuyển INSPECTING → WAITING_APPROVAL
    BookingResponse approve(Long bookingId);                        // chuyển WAITING_APPROVAL → IN_PROGRESS
    BookingResponse reject(Long bookingId, String reason);          // từ chối báo giá, quay về INSPECTING
    BookingResponse complete(Long bookingId);                       // chuyển IN_PROGRESS → COMPLETE
    BookingResponse abort(Long bookingId, String reason);           // chuyển IN_PROGRESS → ABORTED
    BookingResponse cancelMaintenance(Long bookingId, String reason);
}
