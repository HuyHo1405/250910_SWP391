package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.InvoiceStatus;
import com.example.demo.repo.*;
import com.example.demo.service.interfaces.IBookingStatusService;
import com.example.demo.service.interfaces.IInvoiceService;
import com.example.demo.utils.BookingResponseMapper; // <-- THAY ĐỔI IMPORT
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingStatusService implements IBookingStatusService {

    private final IInvoiceService invoiceService;
    private final AccessControlService accessControlService;

    private final BookingRepo bookingRepository;
    private final MaintenanceCatalogModelPartRepo maintenanceCatalogModelPartRepo;
    private final PartRepo partRepo;
    private final InvoiceRepo invoiceRepo;

    // Các trạng thái cho phép cancel
    private final List<BookingStatus> CANCELLABLE_STATUSES = Arrays.asList(
            BookingStatus.PENDING
    );
    private final JobRepo jobRepo;
    private final JobService jobService;

    @Override
    @Transactional
    public BookingResponse confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyCanAccessAllResources( "BOOKING", "confirm");

        if(!checkEnoughPartForBooking(booking.getId())) {
            throw new CommonException.InvalidOperation("Không đủ số lượng linh kiện cần thiết để thực hiện đơn");
        }

        updateReservedParts(booking);

        // Chỉ cho phép xác nhận nếu đang ở trạng thái PENDING
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new CommonException.InvalidOperation("Chỉ có thể xác nhận các đặt lịch ở trạng thái PENDING");
        }

        // Chuyển trạng thái sang CONFIRMED
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        invoiceService.create(id);
        bookingRepository.save(booking);

        // Trả về DTO đầy đủ (bao gồm cả hóa đơn vừa tạo)
        return BookingResponseMapper.toDtoFull(booking);
    }


    @Override
    public BookingResponse startMaintenance(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyCanAccessAllResources( "BOOKING", "start-maintenance");

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new CommonException.InvalidOperation("Đặt lịch chưa được xác nhận cho thao tác này");
        }

        booking.setBookingStatus(BookingStatus.IN_PROGRESS);

        usePartsForMaintenance(booking);

        // Trả về DTO đầy đủ
        return BookingResponseMapper.toDtoFull(booking);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "cancel");

        // Kiểm tra trạng thái đã bị hủy chưa
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new CommonException.InvalidOperation("Booking is already cancelled");
        }

        // Kiểm tra trạng thái có cho phép cancel không
        if (!CANCELLABLE_STATUSES.contains(booking.getBookingStatus())) {
            throw new CommonException.InvalidOperation(
                    "Không thể hủy đặt lịch ở trạng thái: " + booking.getBookingStatus() +
                            ". Chỉ có thể hủy các đặt lịch ở trạng thái PENDING."
            );
        }

        // Cập nhật trạng thái thành CANCELLED
        booking.setBookingStatus(BookingStatus.CANCELLED);

        log.info("Booking {} cancelled. Reason: {}", id, reason);

        // Trả về DTO đầy đủ
        return BookingResponseMapper.toDtoFull(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse completeMaintenance(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyCanAccessAllResources("BOOKING", "complete");

        // Kiểm tra trạng thái trước khi complete
        if (booking.getBookingStatus() != BookingStatus.IN_PROGRESS) {
            throw new CommonException.InvalidOperation(
                    "Không thể hoàn thành đặt lịch chưa được bắt đầu"
            );
        }

        // Chuyển sang trạng thái hoàn thành
        booking.setBookingStatus(BookingStatus.MAINTENANCE_COMPLETE);

        Invoice invoice = booking.getInvoice();
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoiceRepo.save(invoice);

        log.info("Booking {} marked as delivered/completed. Invoice was recalculated and finalized.", id);
        // Trả về DTO đầy đủ (bao gồm cả hóa đơn đã cập nhật trạng thái)
        return BookingResponseMapper.toDtoFull(bookingRepository.save(booking));
    }

    @Override
    public boolean checkEnoughPartForBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));

        if(booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new CommonException.InvalidOperation(
                    "Đơn đã được xác nhận không cần kiểm tra số lượng"
            );
        }

        for (BookingDetail detail : booking.getBookingDetails()) {

            // 1. Lấy catalogModelId trực tiếp từ booking detail
            Long catalogModelId = detail.getCatalogModel().getId();

            // 2. Lấy danh sách part (cháu) từ catalogModelId (con)
            List<MaintenanceCatalogModelPart> requiredParts =
                    maintenanceCatalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId);

            for (MaintenanceCatalogModelPart mp : requiredParts) {
                Part part = mp.getPart();
                BigDecimal available = part.getQuantity().subtract(part.getReserved());
                if (available.compareTo(mp.getQuantityRequired()) < 0) {
                    return false; // thiếu part
                }
            }
        }
        return true; // đủ hết part
    }

    private void updateReservedParts(Booking booking) {
        for (BookingDetail detail : booking.getBookingDetails()) {

            // 1. Lấy catalogModelId trực tiếp từ booking detail
            Long catalogModelId = detail.getCatalogModel().getId();

            // 2. Lấy danh sách part (cháu) từ catalogModelId (con)
            List<MaintenanceCatalogModelPart> requiredParts =
                    maintenanceCatalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId);

            for (MaintenanceCatalogModelPart mp : requiredParts) {
                Part part = mp.getPart();
                part.setReserved(part.getReserved().add(mp.getQuantityRequired()));
                partRepo.save(part);
            }
        }
    }

    private void usePartsForMaintenance(Booking booking) {
        for (BookingDetail detail : booking.getBookingDetails()) {

            // 1. Lấy catalogModelId trực tiếp từ booking detail
            Long catalogModelId = detail.getCatalogModel().getId();

            // 2. Lấy danh sách part (cháu) từ catalogModelId (con)
            List<MaintenanceCatalogModelPart> requiredParts =
                    maintenanceCatalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId);

            for (MaintenanceCatalogModelPart mp : requiredParts) {
                Part part = mp.getPart();
                BigDecimal qty = mp.getQuantityRequired();

                part.setQuantity(part.getQuantity().subtract(qty));
                part.setReserved(part.getReserved().subtract(qty));
                partRepo.save(part);

            }
        }
    }
}
