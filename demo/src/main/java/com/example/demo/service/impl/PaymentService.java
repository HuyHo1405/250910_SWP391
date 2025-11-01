package com.example.demo.service.impl;

import com.example.demo.exception.BookingException;
import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.Invoice;
import com.example.demo.model.modelEnum.InvoiceStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.InvoiceRepo;
import com.example.demo.service.interfaces.IPaymentService;
import com.example.demo.utils.BookingResponseMapper; // <-- ĐÃ CẬP NHẬT IMPORT
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final BookingRepo bookingRepo;
    private final InvoiceRepo invoiceRepo;
    private final AccessControlService accessControlService;

    private Booking findBookingAndVerifyAccess(Long bookingId, String action) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));

        accessControlService.verifyResourceAccess(
                booking.getCustomer().getId(),
                "PAYMENT",
                action
        );

        return booking;
    }

    private Invoice getInvoiceAndValidate(Long bookingId, InvoiceStatus requiredStatus, InvoiceStatus targetStatus) {
        Invoice invoice = invoiceRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Invoice for Booking", bookingId));

        InvoiceStatus currentStatus = invoice.getStatus();

        // Kiểm tra nếu không ở trạng thái yêu cầu
        if (currentStatus != requiredStatus) {
            throw new BookingException.InvalidStatusTransition(
                    "Hóa đơn",
                    currentStatus.name(),
                    targetStatus.name()
            );
        }

        return invoice;
    }

    @Transactional
    public BookingResponse updateInvoiceStatus(
            Booking booking,
            InvoiceStatus newStatus,
            String reason
    ) {
        Invoice invoice = invoiceRepo.findByBookingId(booking.getId())
                .orElseThrow(() -> new CommonException.NotFound("Invoice for Booking", booking.getId()));

        invoice.setStatus(newStatus);

        invoiceRepo.save(invoice);

        // Trả về DTO đầy đủ (Booking + Details + Invoice)
        return BookingResponseMapper.toDtoFull(booking); // <-- ĐÃ THAY ĐỔI
    }

    @Transactional
    @Override
    public BookingResponse pay(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "pay");

        // Chỉ cho phép pay từ UNPAID
        getInvoiceAndValidate(bookingId, InvoiceStatus.UNPAID, InvoiceStatus.PAID);

        return updateInvoiceStatus(booking, InvoiceStatus.PAID, null);
    }

    @Transactional
    @Override
    public BookingResponse refund(Long bookingId, String reason) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "refund");

        // Chỉ cho phép refund từ PAID
        getInvoiceAndValidate(bookingId, InvoiceStatus.PAID, InvoiceStatus.REFUNDED);

        return updateInvoiceStatus(booking, InvoiceStatus.REFUNDED, reason);
    }

    @Transactional
    @Override
    public BookingResponse cancelPayment(Long bookingId, String reason) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "cancel");

        // Chỉ cho phép cancel từ UNPAID
        getInvoiceAndValidate(bookingId, InvoiceStatus.UNPAID, InvoiceStatus.CANCELLED);

        return updateInvoiceStatus(booking, InvoiceStatus.CANCELLED, reason);
    }
}
