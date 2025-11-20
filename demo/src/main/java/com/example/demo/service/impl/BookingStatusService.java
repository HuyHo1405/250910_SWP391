package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingResponse;
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
import java.util.Objects;

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
    private final JobRepo jobRepo;
    private final UserRepo userRepo;

    private static final long JOB_BUFFER_MINUTES = 60;

    // Các trạng thái cho phép cancel (customer)
    private final List<BookingStatus> CANCELLABLE_STATUSES = Arrays.asList(
            BookingStatus.PENDING
    );

    // Các trạng thái cho phép reject (staff/admin)
    private final List<BookingStatus> REJECTABLE_STATUSES = Arrays.asList(
            BookingStatus.PENDING
    );

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
    @Transactional
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


        //give staff/technician time to handle works
        invoice.setDueDate(booking.getScheduleDate().minusHours(1));
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoiceRepo.save(invoice);

        // Trả về DTO đầy đủ (bao gồm cả hóa đơn vừa tạo)
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

        booking = createJobForBooking(booking, technician);

        return BookingResponseMapper.toDtoFull(booking);
    }

    @Override
    public BookingResponse reassignTechnician(Long id, Long newTechnicianId, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyCanAccessAllResources("JOB", "UPDATE");

        log.info("=== EMERGENCY REASSIGNMENT STARTED for Booking #{} ===", id);

        // 1. Validate booking has a job
        Job job = booking.getJob();
        if (job == null) {
            log.error("Booking #{} does not have an associated Job", id);
            throw new CommonException.NotFound("Job for Booking", id);
        }

        log.debug("Job #{} found for Booking #{}", job.getId(), id);

        // 2. Cannot reassign if already started
        if (job.getStartTime() != null) {
            log.warn("Cannot reassign Job #{} - already started at {}", job.getId(), job.getStartTime());
            throw new CommonException.InvalidOperation(
                    "JOB_ALREADY_STARTED",
                    "Không thể reassign job đã bắt đầu"
            );
        }

        // 3. Must be in ASSIGNED or PAID status
        if (booking.getBookingStatus() != BookingStatus.ASSIGNED &&
                booking.getBookingStatus() != BookingStatus.PAID) {
            log.warn("Cannot reassign Booking #{} - invalid status: {}", id, booking.getBookingStatus());
            throw new CommonException.InvalidOperation(
                    "INVALID_STATUS",
                    "Chỉ có thể reassign booking ở trạng thái ASSIGNED hoặc PAID"
            );
        }

        User oldTechnician = job.getTechnician();
        log.info("Current technician for Job #{}: {} (ID: {})",
                job.getId(),
                oldTechnician != null ? oldTechnician.getFullName() : "Unassigned",
                oldTechnician != null ? oldTechnician.getId() : "N/A");

        // 4. Validate new technician
        User newTechnician = userRepo.findById(newTechnicianId)
                .orElseThrow(() -> new CommonException.NotFound("Technician", newTechnicianId));

        if (!newTechnician.getRole().getName().equals("TECHNICIAN")) {
            log.error("User #{} ({}) does not have TECHNICIAN role: {}",
                    newTechnicianId,
                    newTechnician.getFullName(),
                    newTechnician.getRole().getName());
            throw new CommonException.InvalidOperation(
                    "INVALID_ROLE",
                    "Người dùng phải có vai trò kỹ thuật viên"
            );
        }

        log.info("New technician validated: {} (ID: {})", newTechnician.getFullName(), newTechnicianId);

        // 5. Check new technician availability at ORIGINAL time
        LocalDateTime originalSchedule = booking.getScheduleDate();
        log.debug("Checking availability for technician #{} at schedule time: {}", newTechnicianId, originalSchedule);

        if (!isTechnicianAvailableAtTime(newTechnicianId, originalSchedule, job.getId())) {
            log.warn("Technician #{} ({}) is NOT available at {}",
                    newTechnicianId,
                    newTechnician.getFullName(),
                    originalSchedule);
            throw new CommonException.InvalidOperation(
                    "TECHNICIAN_NOT_AVAILABLE",
                    String.format("Kỹ thuật viên không rảnh vào thời điểm %s", originalSchedule)
            );
        }

        log.info("Technician #{} ({}) is AVAILABLE at {}",
                newTechnicianId,
                newTechnician.getFullName(),
                originalSchedule);

        // 6. Perform reassignment
        job.setTechnician(newTechnician);

        String reassignNote = String.format(
                "[%s] EMERGENCY REASSIGN: %s → %s\nLý do: %s",
                LocalDateTime.now(),
                oldTechnician != null ? oldTechnician.getFullName() : "Unassigned",
                newTechnician.getFullName(),
                reason
        );

        job.setNotes(job.getNotes() != null ?
                job.getNotes() + "\n" + reassignNote : reassignNote);

        jobRepo.save(job);

        log.info("=== EMERGENCY REASSIGNMENT COMPLETED ===");
        log.info("Booking #{} | Job #{}", id, job.getId());
        log.info("Schedule: {}", originalSchedule);
        log.info("Old Technician: {} (ID: {})",
                oldTechnician != null ? oldTechnician.getFullName() : "None",
                oldTechnician != null ? oldTechnician.getId() : "N/A");
        log.info("New Technician: {} (ID: {})", newTechnician.getFullName(), newTechnicianId);
        log.info("Reason: {}", reason);
        log.info("Status: {}", booking.getBookingStatus());
        log.info("========================================");

        // Log no-show incident for old technician (for tracking/analytics)
        if (oldTechnician != null) {
            log.warn("NO-SHOW INCIDENT | Technician #{} ({}) | Booking #{} | Schedule: {} | Reason: {}",
                    oldTechnician.getId(),
                    oldTechnician.getFullName(),
                    id,
                    originalSchedule,
                    reason);
        }

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

    @Scheduled(cron = "0 0/15 * * * *")
    @Transactional
    public void autoCancelOverdueUnpaidBookings() {
        log.info("[Scheduler] Scanning for overdue UNPAID bookings...");

        LocalDateTime now = LocalDateTime.now();

        // 1. Find all bookings that are CONFIRMED (Waiting for payment)
        // Note: You might want to create a specific query in Repo for performance if data is large
        List<Booking> confirmedBookings = bookingRepository.findByBookingStatus(BookingStatus.CONFIRMED);

        for (Booking booking : confirmedBookings) {
            Invoice invoice = booking.getInvoice();

            // Safety check: Invoice exists and is UNPAID
            if (invoice != null && invoice.getStatus() == InvoiceStatus.UNPAID) {

                // 2. Check if DueDate has passed
                if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(now)) {

                    log.info("Booking #{} is overdue (Due: {}). Cancelling...",
                            booking.getId(), invoice.getDueDate());

                    // 3. Cancel Booking
                    booking.setBookingStatus(BookingStatus.CANCELLED);

                    // 4. Cancel Invoice
                    invoice.setStatus(InvoiceStatus.CANCELLED);
                    invoiceRepo.save(invoice); // Explicit save for invoice

                    // 5. Release Reserved Parts (CRITICAL)
                    // Because 'confirmBooking' reserved them, we must un-reserve them now.
                    unreserveParts(booking);

                    bookingRepository.save(booking);
                }
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

    private Booking createJobForBooking(Booking booking, User technician) {
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
        booking.setBookingStatus(BookingStatus.ASSIGNED);
        log.info("Created unassigned Job for Booking #{}", booking.getId());
        return bookingRepository.save(booking); // Cập nhật lại booking nếu cần
    }

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

    private long calculateTotalDuration(Booking booking) {
        if (booking.getBookingDetails() == null) return 0;
        Double totalMinutes = booking.getBookingDetails().stream()
                .map(detail -> detail.getCatalogModel().getEstTimeMinutes())
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum);
        return totalMinutes.longValue();
    }

    public boolean isTechnicianAvailableAtTime(Long technicianId, LocalDateTime scheduleTime, Long excludeJobId) {
        // 1. Get all incomplete jobs for this technician
        List<Job> technicianJobs = jobRepo.findByTechnicianIdAndNotComplete(technicianId);
        LocalDateTime now = LocalDateTime.now();

        if(technicianJobs.isEmpty()){
            return true; // Rảnh nếu không có job nào
        }

        for (Job job : technicianJobs) {
            // Skip the job being updated (if applicable)
            if (excludeJobId != null && job.getId().equals(excludeJobId)) {
                continue;
            }

            LocalDateTime jobStart;
            LocalDateTime jobEndEffective;

            if (job.getStartTime() != null) {
                // --- CASE A: Job STARTED ---
                // Logic: If working late, extend the window to NOW.
                jobStart = job.getStartTime();
                LocalDateTime estimatedEnd = job.getEstEndTime();

                if (now.isAfter(estimatedEnd)) {
                    jobEndEffective = now; // Running late: Busy until at least now
                } else {
                    jobEndEffective = estimatedEnd; // On time: Busy until estimate
                }
            } else {
                // --- CASE B: Job ASSIGNED but NOT STARTED ---
                // Logic: Strictly follow Schedule + Duration. Do not check 'Now'.
                jobStart = job.getBooking().getScheduleDate();

                long durationMinutes = calculateTotalDuration(job.getBooking());
                jobEndEffective = jobStart.plusMinutes(durationMinutes);
            }

            // --- Add Buffer (Preparation + Cleaning) ---
            LocalDateTime busyUntil = jobEndEffective.plusMinutes(JOB_BUFFER_MINUTES);

            // --- Check Intersection ---
            // If requested scheduleTime is inside [jobStart, busyUntil)
            if (!scheduleTime.isBefore(jobStart) && scheduleTime.isBefore(busyUntil)) {
                return false; // Technician is busy
            }
        }

        return true; // Technician is free
    }
}
