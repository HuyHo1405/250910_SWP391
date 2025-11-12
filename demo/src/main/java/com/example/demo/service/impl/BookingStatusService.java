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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final PaymentRepo paymentRepo;

    // Các trạng thái cho phép cancel (customer)
    private final List<BookingStatus> CANCELLABLE_STATUSES = Arrays.asList(
            BookingStatus.PENDING
    );

    // Các trạng thái cho phép reject (staff/admin)
    private final List<BookingStatus> REJECTABLE_STATUSES = Arrays.asList(
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
    public BookingResponse rejectBooking(Long id, String reason) {
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

        log.info("Booking {} cancelled. Reason: {}", id, reason);

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

        // Tự động tạo unassigned Jobs cho mỗi BookingDetail
        createUnassignedJobsForBooking(booking);

        log.info("Booking {} started maintenance. Created {} unassigned jobs.", id, booking.getBookingDetails().size());

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

        // ← THÊM MỚI: Kiểm tra tất cả jobs đã hoàn thành chưa
        checkAllJobsCompleted(booking);

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

    private void createUnassignedJobsForBooking(Booking booking) {
        for (BookingDetail detail : booking.getBookingDetails()) {
            // Kiểm tra xem BookingDetail này đã có Job chưa
            if (jobRepo.findByBookingDetailId(detail.getId()).isPresent()) {
                log.warn("BookingDetail {} already has a Job, skipping creation", detail.getId());
                continue;
            }

            // Tạo Job mới với technician = null (unassigned)
            Job job = Job.builder()
                    .bookingDetail(detail)
                    .technician(null) // chưa assign technician
                    .notes("Auto-created job for booking #" + booking.getId())
                    .build();

            jobRepo.save(job);
            log.info("Created unassigned Job for BookingDetail #{}", detail.getId());
        }
    }

    /**
     * Kiểm tra tất cả jobs của booking đã hoàn thành chưa
     * @param booking Booking cần kiểm tra
     * @throws CommonException.InvalidOperation nếu còn job chưa hoàn thành
     */
    private void checkAllJobsCompleted(Booking booking) {
        List<Job> incompleteJobs = new ArrayList<>();

        for (BookingDetail detail : booking.getBookingDetails()) {
            Optional<Job> jobOpt = jobRepo.findByBookingDetailId(detail.getId());

            if (jobOpt.isEmpty()) {
                // Có BookingDetail nhưng chưa có Job
                throw new CommonException.InvalidOperation(
                    String.format("BookingDetail #%d chưa có Job được tạo", detail.getId())
                );
            }

            Job job = jobOpt.get();

            // Kiểm tra job đã hoàn thành chưa (actualEndTime != null)
            if (job.getActualEndTime() == null) {
                incompleteJobs.add(job);
            }
        }

        // Nếu có job chưa hoàn thành, throw exception
        if (!incompleteJobs.isEmpty()) {
            String jobIds = incompleteJobs.stream()
                    .map(job -> "#" + job.getId())
                    .collect(Collectors.joining(", "));

            throw new CommonException.InvalidOperation(
                String.format(
                    "Không thể hoàn thành booking. Còn %d job chưa hoàn thành: %s",
                    incompleteJobs.size(),
                    jobIds
                )
            );
        }

        log.info("All jobs for Booking #{} are completed. Proceeding to complete maintenance.", booking.getId());
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
