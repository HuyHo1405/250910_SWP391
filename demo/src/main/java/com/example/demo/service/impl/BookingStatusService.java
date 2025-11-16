package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.PaymentResponse;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.InvoiceStatus;
import com.example.demo.repo.*;
import com.example.demo.service.interfaces.IBookingStatusService;
import com.example.demo.utils.BookingResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    private final UserRepo userRepo;
//    private final JobService jobService;
    private final PaymentService paymentService;

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

        log.info("Booking {} rejected.", id);

        // Trả về DTO đầy đủ
        return BookingResponseMapper.toDtoFull(bookingRepository.save(booking));

    }

    @Override
    @Transactional
    public BookingResponse startMaintenance(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

//        accessControlService.verifyCanAccessAllResources( "BOOKING", "start-maintenance");

        // 🔄 THAY ĐỔI: Phải PAID mới được bắt đầu
        if (booking.getBookingStatus() != BookingStatus.PAID) {
            throw new CommonException.InvalidOperation(
                    "Chưa thanh toán, không thể bắt đầu bảo trì. Trạng thái hiện tại: " + booking.getBookingStatus()
            );
        }

        booking.setBookingStatus(BookingStatus.IN_PROGRESS);
        usePartsForMaintenance(booking);

        // Tạo Job duy nhất cho Booking (One-to-One)
//        createJobForBooking(booking, technician);

        log.info("Booking {} started maintenance. Created job.", id);

        // Trả về DTO đầy đủ
        return BookingResponseMapper.toDtoFull(booking);
    }

    @Override
    public BookingResponse assignTechnician(Long id, Long technicianId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyCanAccessAllResources( "JOB", "create");

        // 🔄 THAY ĐỔI: Phải PAID mới được bắt đầu
        if (booking.getBookingStatus() != BookingStatus.PAID) {
            throw new CommonException.InvalidOperation(
                    "Chưa thanh toán, không thể phân công. Trạng thái hiện tại: " + booking.getBookingStatus()
            );
        }

        User technician = userRepo.findById(technicianId)
                .orElseThrow(() -> new CommonException.NotFound("User Technician", technicianId));

        boolean checkAvailable = isTechnicianAvailableAtTime(technicianId, booking.getScheduleDate(), null);

        if (!checkAvailable) {
            throw new CommonException.InvalidOperation(
                    "Kỹ thuật viên không có sẵn vào thời gian đã lên lịch: " + booking.getScheduleDate()
            );
        }

        createJobForBooking(booking, technician);

        return BookingResponseMapper.toDtoFull(booking);
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

    // Scheduled job: cancel & refund PAID bookings quá hạn chưa start
    @Scheduled(cron = "0 0 * * * *") // Chạy mỗi giờ
    @Transactional
    public void cancelAndRefundOverduePaidBookings() {
        int additionalMinutes = 30; // Có thể lấy từ config
        LocalDateTime now = LocalDateTime.now();
        // Lấy tất cả booking PAID có scheduleDate <= now
        List<Booking> paidBookings = bookingRepository.findByBookingStatus(BookingStatus.PAID);
        for (Booking booking : paidBookings) {
            // Nếu đã quá hạn: scheduleDate + additionalMinutes < now
            LocalDateTime deadline = booking.getScheduleDate().plusMinutes(additionalMinutes);
            if (deadline.isBefore(now) && booking.getBookingStatus() == BookingStatus.PAID) {
                // Cancel booking
                booking.setBookingStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                // Refund invoice
                Invoice invoice = booking.getInvoice();
                if (invoice != null && invoice.getStatus() == InvoiceStatus.PAID) {
                    invoice.setStatus(InvoiceStatus.REFUNDED);
                    invoiceRepo.save(invoice);
                    PaymentResponse.RefundResult result = paymentService.createRefundByInvoiceId(invoice.getId());
                    paymentService.simulateRefund(result.getOrderCode());
                    log.info("[Scheduler] Đã tạo yêu cầu refund cho invoice ID {} của booking ID {}.", invoice.getId(), booking.getId());
                }


                // Giải phóng linh kiện đã reserved
                unreserveParts(booking);
                log.info("[Scheduler] Đã cancel & refund booking ID {} do quá hạn chưa start.", booking.getId());
            }
        }
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

    private void createJobForBooking(Booking booking, User technician) {
        // Kiểm tra xem Booking đã có Job chưa
        if (jobRepo.findByBookingId(booking.getId()).isPresent()) {
            log.warn("Booking {} already has a Job, skipping creation", booking.getId());
            throw new CommonException.InvalidOperation("Booking đã có Job, không thể tạo thêm");
        }



        // Tạo Job mới với technician = null (unassigned)
        Job job = Job.builder()
                .booking(booking)
                .technician(technician)
                .notes("Auto-created job for booking #" + booking.getId())
                .build();

        jobRepo.save(job);

        booking.setJob(job);
        bookingRepository.save(booking); // Cập nhật lại booking nếu cần
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

    private boolean isTechnicianAvailableAtTime(Long technicianId, LocalDateTime scheduleTime, Long excludeJobId) {
        // Lấy tất cả jobs của technician này (chưa complete)
        List<Job> technicianJobs = jobRepo.findByTechnicianIdAndNotComplete(technicianId);

        // Lọc ra jobs trùng giờ
        for (Job job : technicianJobs) {
            // Bỏ qua job đang update
            if (excludeJobId != null && job.getId().equals(excludeJobId)) {
                continue;
            }

            // Lấy schedule time của booking
            LocalDateTime jobScheduleTime = job.getBooking().getScheduleDate();

            // Kiểm tra trùng giờ (cùng giờ tròn)
            // VD: 09:00:00 == 09:00:00
            if (jobScheduleTime.withMinute(0).withSecond(0).equals(scheduleTime.withMinute(0).withSecond(0))) {
                return false; // Technician bận vào giờ này
            }
        }

        return true; // Technician rảnh
    }
}
