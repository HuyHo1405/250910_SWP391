package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.EntityStatus;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingStatusService implements IBookingStatusService {

    private final AccessControlService accessControlService;

    private final BookingRepo bookingRepository;
    private final MaintenanceCatalogModelPartRepo maintenanceCatalogModelPartRepo;
    private final PartRepo partRepo;
    private final InvoiceRepo invoiceRepo;

    // Các trạng thái cho phép cancel (customer)
    private final List<BookingStatus> CANCELLABLE_STATUSES = Arrays.asList(
            BookingStatus.PENDING
    );

    // Các trạng thái cho phép reject (staff/admin)
    private final List<BookingStatus> REJECTABLE_STATUSES = Arrays.asList(
            BookingStatus.PENDING
    );

    private final JobRepo jobRepo;

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
        booking = bookingRepository.save(booking);

        // Chuyển invoice thành DRAFT thành UNPAID và đặt dueDate = scheduleDate
        Invoice invoice = booking.getInvoice();

        if(invoice == null || invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new CommonException.InvalidOperation("Invoice không tồn tại hoặc không ở trạng thái DRAFT");
        }

        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setDueDate(booking.getScheduleDate());
        invoiceRepo.save(invoice);

        // Trả về DTO đầy đủ (bao gồm cả hóa đơn vừa tạo)
        return BookingResponseMapper.toDtoFull(booking);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id) {
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

        log.info("Booking {} cancelled.", id);

        // Trả về DTO đầy đủ
        return BookingResponseMapper.toDtoFull(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse rejectBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "reject");

        // Kiểm tra trạng thái đã bị hủy chưa
        if (booking.getBookingStatus() == BookingStatus.REJECTED) {
            throw new CommonException.InvalidOperation("Booking is already rejected");
        }

        // Kiểm tra trạng thái có cho phép cancel không
        if (!REJECTABLE_STATUSES.contains(booking.getBookingStatus())) {
            throw new CommonException.InvalidOperation(
                    "Không thể từ chối đặt lịch ở trạng thái: " + booking.getBookingStatus() +
                            ". Chỉ có thể từ chối các đặt lịch ở trạng thái PENDING."
            );
        }

        // Cập nhật trạng thái thành CANCELLED
        booking.setBookingStatus(BookingStatus.REJECTED);

        log.info("Booking {} cancelled.", id);

        // Trả về DTO đầy đủ
        return BookingResponseMapper.toDtoFull(bookingRepository.save(booking));

    }

    @Override
    @Transactional
    public BookingResponse startMaintenance(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyCanAccessAllResources( "BOOKING", "start-maintenance");

        // 🔄 THAY ĐỔI: Phải PAID mới được bắt đầu
        if (booking.getBookingStatus() != BookingStatus.PAID) {
            throw new CommonException.InvalidOperation(
                    "Chưa thanh toán, không thể bắt đầu bảo trì. Trạng thái hiện tại: " + booking.getBookingStatus()
            );
        }

        booking.setBookingStatus(BookingStatus.IN_PROGRESS);

        usePartsForMaintenance(booking);

        // Tạo Job duy nhất cho Booking (One-to-One)
        createJobForBooking(booking);

        log.info("Booking {} started maintenance. Created job.", id);

        // Trả về DTO đầy đủ
        return BookingResponseMapper.toDtoFull(booking);
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

        // Kiểm tra job đã hoàn thành chưa
        Optional<Job> jobOpt = jobRepo.findByBookingId(booking.getId());
        if (jobOpt.isEmpty()) {
            throw new CommonException.InvalidOperation("Booking chưa có Job");
        }

        Job job = jobOpt.get();
        if(job.getTechnician() == null) {
            throw new CommonException.InvalidOperation("Job chưa được phân công kỹ thuật viên");
        }

        if (job.getActualEndTime() == null) {
            throw new CommonException.InvalidOperation("Job chưa hoàn thành. Technician phải hoàn thành job trước.");
        }

        // Chuyển sang trạng thái hoàn thành
        booking.setBookingStatus(BookingStatus.MAINTENANCE_COMPLETE);

        log.info("Booking {} marked as delivered/completed. Invoice was recalculated and finalized.", id);
        // Trả về DTO đầy đủ (bao gồm cả hóa đơn đã cập nhật trạng thái)
        return BookingResponseMapper.toDtoFull(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
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
                if(part.getStatus() == EntityStatus.INACTIVE) {
                    return false;
                }

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

                // Trừ quantity (số lượng trong kho giảm)
                part.setQuantity(part.getQuantity().subtract(qty));
                // Trừ reserved (số lượng đã đặt giảm)
                part.setReserved(part.getReserved().subtract(qty));
                // Cộng used (số lượng đã sử dụng tăng)
                part.setUsed(part.getUsed().add(qty));

                partRepo.save(part);
                log.info("Part {} used: {} units. New stock: quantity={}, reserved={}, used={}",
                    part.getName(), qty, part.getQuantity(), part.getReserved(), part.getUsed());
            }
        }
    }

    private void createJobForBooking(Booking booking) {
        // Kiểm tra xem Booking đã có Job chưa
        if (jobRepo.findByBookingId(booking.getId()).isPresent()) {
            log.warn("Booking {} already has a Job, skipping creation", booking.getId());
            throw new CommonException.InvalidOperation("Booking đã có Job, không thể tạo thêm");
        }

        // Tạo Job mới với technician = null (unassigned)
        Job job = Job.builder()
                .booking(booking)
                .technician(null) // chưa assign technician
                .notes("Auto-created job for booking #" + booking.getId())
                .build();

        jobRepo.save(job);
        log.info("Created unassigned Job for Booking #{}", booking.getId());
    }

    /**
     * Giải phóng parts đã reserved khi reject booking
     */
    private void unreserveParts(Booking booking) {
        for (BookingDetail detail : booking.getBookingDetails()) {
            Long catalogModelId = detail.getCatalogModel().getId();
            List<MaintenanceCatalogModelPart> requiredParts =
                    maintenanceCatalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId);

            for (MaintenanceCatalogModelPart mp : requiredParts) {
                Part part = mp.getPart();
                part.setReserved(part.getReserved().subtract(mp.getQuantityRequired()));
                partRepo.save(part);
                log.info("Part {} unreserved: {} units. New reserved: {}",
                    part.getName(), mp.getQuantityRequired(), part.getReserved());
            }
        }
    }
}
