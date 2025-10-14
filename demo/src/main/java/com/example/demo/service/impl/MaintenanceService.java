package com.example.demo.service.impl;

import com.example.demo.exception.BookingException; // Import mới
import com.example.demo.exception.CommonException;  // Import mới
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.modelEnum.MaintenanceStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.service.interfaces.IMaintenanceService;
import com.example.demo.utils.BookingResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MaintenanceService implements IMaintenanceService {

    private final AccessControlService accessControlService;
    private final BookingRepo bookingRepo;

    private Booking findBookingAndVerifyAccess(Long bookingId, String action) {
        Booking booking = bookingRepo.findById(bookingId)
                // ✅ Sửa: Dùng NotFound
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "MAINTENANCE", action);

        MaintenanceStatus currentStatus = booking.getMaintenanceStatus();

        if (currentStatus == MaintenanceStatus.COMPLETE || currentStatus == MaintenanceStatus.ABORTED) {
            // ✅ Sửa: Dùng exception cụ thể cho việc bảo dưỡng đã hoàn thành
            throw new BookingException.MaintenanceAlreadyCompleted();
        }

        // ✅ Sửa: Dùng InvalidStatusTransition cho tất cả các lỗi quy trình
        switch (action) {
            case "start-inspection":
                if (currentStatus != MaintenanceStatus.IDLE) {
                    throw new BookingException.InvalidStatusTransition("Maintenance", currentStatus.name(), "INSPECTING");
                }
                break;
            case "request-approval":
                if (currentStatus != MaintenanceStatus.INSPECTING) {
                    throw new BookingException.InvalidStatusTransition("Maintenance", currentStatus.name(), "WAITING_APPROVAL");
                }
                break;
            case "approve":
            case "reject":
                if (currentStatus != MaintenanceStatus.WAITING_APPROVAL) {
                    throw new BookingException.InvalidStatusTransition("Maintenance", currentStatus.name(), "IN_PROGRESS or INSPECTING");
                }
                break;
            case "complete":
                if (currentStatus != MaintenanceStatus.IN_PROGRESS) {
                    throw new BookingException.InvalidStatusTransition("Maintenance", currentStatus.name(), "COMPLETE");
                }
                break;
        }

        return booking;
    }

    @Transactional
    public BookingResponse updateMaintenanceStatus(Booking booking, MaintenanceStatus status, String reason) {
        booking.setMaintenanceStatus(status);
        bookingRepo.save(booking);
        return BookingResponseMapper.toDto(booking);
    }

    // --- Các phương thức public không cần thay đổi ---

    @Transactional
    @Override
    public BookingResponse startInspection(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "start-inspection");
        return updateMaintenanceStatus(booking, MaintenanceStatus.INSPECTING, null);
    }

    @Transactional
    @Override
    public BookingResponse requestApproval(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "request-approval");
        return updateMaintenanceStatus(booking, MaintenanceStatus.WAITING_APPROVAL, null);
    }

    @Transactional
    @Override
    public BookingResponse approve(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "approve");
        return updateMaintenanceStatus(booking, MaintenanceStatus.IN_PROGRESS, null);
    }

    @Transactional
    @Override
    public BookingResponse reject(Long bookingId, String reason) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "reject");
        return updateMaintenanceStatus(booking, MaintenanceStatus.INSPECTING, reason);
    }

    @Transactional
    @Override
    public BookingResponse complete(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "complete");
        return updateMaintenanceStatus(booking, MaintenanceStatus.COMPLETE, null);
    }

    @Transactional
    @Override
    public BookingResponse abort(Long bookingId, String reason) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "abort");
        return updateMaintenanceStatus(booking, MaintenanceStatus.ABORTED, reason);
    }

    @Transactional
    @Override
    public BookingResponse cancelMaintenance(Long bookingId, String reason) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "cancel");
        return updateMaintenanceStatus(booking, MaintenanceStatus.ABORTED, reason);
    }
}