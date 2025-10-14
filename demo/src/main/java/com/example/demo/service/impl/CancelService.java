package com.example.demo.service.impl;

import com.example.demo.exception.BookingException; // Import mới
import com.example.demo.exception.CommonException;  // Import mới
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.modelEnum.BookingLifecycle;
import com.example.demo.model.modelEnum.MaintenanceStatus;
import com.example.demo.model.modelEnum.ScheduleStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.service.interfaces.ICancelService;
import com.example.demo.service.interfaces.IMaintenanceService;
import com.example.demo.service.interfaces.IPaymentService;
import com.example.demo.service.interfaces.IScheduleService;
import com.example.demo.utils.BookingResponseMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CancelService implements ICancelService {

    private final IScheduleService scheduleService;
    private final IMaintenanceService maintenanceService;
    private final IPaymentService paymentService;
    private final BookingRepo bookingRepo;
    private final UserContextService userContextService;

    private final List<ScheduleStatus> allowedStatuses = Arrays.asList(
            ScheduleStatus.PENDING,
            ScheduleStatus.CONFIRMED,
            ScheduleStatus.CHECKED_IN
    );

    @Override
    @Transactional
    public BookingResponse cancelAll(Long bookingId, String reason) {
        Booking booking = bookingRepo.findById(bookingId)
                // ✅ Sửa: Dùng NotFound
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));

        checkCancellationPolicy(booking, reason);

        if (booking.getScheduleStatus() != null) {
            scheduleService.cancelSchedule(bookingId, reason);
        }
        if (booking.getMaintenanceStatus() != null) {
            maintenanceService.cancelMaintenance(bookingId, reason);
        }
        if (booking.getPaymentStatus() != null) {
            paymentService.cancelPayment(bookingId, reason);
        }

        booking.setLifecycleStatus(BookingLifecycle.CANCELLED);
        bookingRepo.save(booking);
        return BookingResponseMapper.toDto(booking);
    }

    private void checkCancellationPolicy(Booking booking, String reason){
        if (booking.getLifecycleStatus() == BookingLifecycle.CANCELLED) {
            // ✅ Sửa: Dùng exception cụ thể hơn
            throw new BookingException.CannotCancel(booking.getLifecycleStatus().name(), "Booking is already cancelled");
        }

        boolean canCancel = false;
        ScheduleStatus scheduleStatus = booking.getScheduleStatus();
        MaintenanceStatus maintenanceStatus = booking.getMaintenanceStatus();

        if (userContextService.isStaffOrAdmin()){
            canCancel = isStaffConditionMet(maintenanceStatus, scheduleStatus);
        } else if(userContextService.isCustomer()){
            canCancel = isCustomerConditionMet(scheduleStatus, maintenanceStatus);
        }

        if(!canCancel){
            // ✅ Sửa: Dùng CannotCancel thay vì Forbidden để cung cấp ngữ cảnh rõ hơn
            throw new BookingException.CannotCancel(
                    scheduleStatus.name(),
                    "Cancellation is not allowed in the current booking state for your role."
            );
        }
    }

    private boolean isCustomerConditionMet(ScheduleStatus scheduleStatus, MaintenanceStatus maintenanceStatus){
        return allowedStatuses.contains(scheduleStatus) &&
                maintenanceStatus.equals(MaintenanceStatus.IDLE);
    }

    private boolean isStaffConditionMet(MaintenanceStatus maintenanceStatus, ScheduleStatus scheduleStatus){
        if (maintenanceStatus.equals(MaintenanceStatus.IDLE)) {
            return allowedStatuses.contains(scheduleStatus);
        } else if (maintenanceStatus.equals(MaintenanceStatus.WAITING_APPROVAL)) {
            return scheduleStatus.equals(ScheduleStatus.CHECKED_IN);
        }
        return false;
    }
}