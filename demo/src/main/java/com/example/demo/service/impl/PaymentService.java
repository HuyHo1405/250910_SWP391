package com.example.demo.service.impl;

import com.example.demo.exception.BookingException; // Import mới
import com.example.demo.exception.CommonException;  // Import mới
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.modelEnum.PaymentStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.service.interfaces.IPaymentService;
import com.example.demo.utils.BookingResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final BookingRepo bookingRepo;
    private final AccessControlService accessControlService;

    private Booking findBookingAndVerifyAccess(Long bookingId, String action) {
        Booking booking = bookingRepo.findById(bookingId)
                // ✅ Sửa: Dùng NotFound
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "PAYMENT", action);

        PaymentStatus currentStatus = booking.getPaymentStatus();

        if (currentStatus == PaymentStatus.REFUNDED || currentStatus == PaymentStatus.VOIDED) {
            // ✅ Sửa: Dùng exception cụ thể cho việc thanh toán đã xử lý
            throw new BookingException.PaymentAlreadyMade();
        }

        if (currentStatus == PaymentStatus.PAID && !"refund".equals(action)) {
            // ✅ Sửa: Dùng InvalidStatusTransition để báo lỗi quy trình
            throw new BookingException.InvalidStatusTransition(
                    "Payment",
                    currentStatus.name(),
                    action
            );
        }

        return booking;
    }

    @Transactional
    public BookingResponse updatePaymentStatus(Booking booking, PaymentStatus status, Double amount, String reason) {
        booking.setPaymentStatus(status);
        if (amount != null && status == PaymentStatus.AUTHORIZED) {
            booking.setTotalPrice(amount);
        }
        bookingRepo.save(booking);
        return BookingResponseMapper.toDto(booking);
    }

    // --- Các phương thức public không cần thay đổi ---

    @Transactional
    @Override
    public BookingResponse authorize(Long bookingId, Double amount) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "authorize");
        return updatePaymentStatus(booking, PaymentStatus.AUTHORIZED, amount, null);
    }

    @Transactional
    @Override
    public BookingResponse pay(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "pay");
        return updatePaymentStatus(booking, PaymentStatus.PAID, null, null);
    }

    @Transactional
    @Override
    public BookingResponse refund(Long bookingId, String reason) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "refund");
        return updatePaymentStatus(booking, PaymentStatus.REFUNDED, null, reason);
    }

    @Transactional
    @Override
    public BookingResponse voidPayment(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "void");
        return updatePaymentStatus(booking, PaymentStatus.VOIDED, null, null);
    }

    @Transactional
    @Override
    public BookingResponse cancelPayment(Long bookingId, String reason) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "cancel");
        return updatePaymentStatus(booking, PaymentStatus.UNPAID, null, reason);
    }
}