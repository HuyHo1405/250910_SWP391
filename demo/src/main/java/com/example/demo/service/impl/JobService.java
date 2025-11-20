package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.JobResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.dto.TechnicianResponse;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.JobStatus;
import com.example.demo.repo.*;
import com.example.demo.service.interfaces.IJobService;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final AccessControlService accessControlService;

    private final JobRepo jobRepo;
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final PartRepo partRepo;
    private final MaintenanceCatalogModelPartRepo maintenanceCatalogModelPartRepo;

    // Define the allowed start window (+- 30 minutes)
    private static final long START_WINDOW_MINUTES = 30;

    // Constant: 45 mins prep + 15 mins cleaning = 60 minutes buffer
    private static final long JOB_BUFFER_MINUTES = 60;

    @Override
    @Transactional
    public JobResponse startJob(Long jobId) {
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId));
        accessControlService.verifyResourceAccess(job.getTechnician().getId(), "JOB", "START");

        if(job.getTechnician() == null){
            throw new CommonException.InvalidOperation("NO_TECHNICIAN_ASSIGNED", "Không thể bắt đầu Job chưa được gán kỹ thuật viên");
        }

        if (job.getStartTime() != null)
            throw new CommonException.Conflict("JOB_ALREADY_STARTED", "Job đã được bắt đầu");

        Booking booking = job.getBooking();

        if(booking.getBookingStatus() != BookingStatus.ASSIGNED){
            throw new CommonException.InvalidOperation("INVALID_STATUS",
                    "Booking phải ở trạng thái ASSIGNED mới được bắt đầu. Trạng thái hiện tại: " + booking.getBookingStatus());
        }

        // ✅ REFACTOR: Gọi hàm validate riêng biệt ở đây
        validateStartJobTime(job, LocalDateTime.now());

        // 1. Trừ kho
        usePartsForMaintenance(booking);

        // 2. Update Status
        booking.setBookingStatus(BookingStatus.IN_PROGRESS);
        bookingRepo.save(booking);

        // 3. Set Time
        long totalEstTimeMinutes = calculateTotalDuration(booking);
        job.setStartTime(LocalDateTime.now());
        job.setEstEndTime(LocalDateTime.now().plusMinutes(totalEstTimeMinutes));

        jobRepo.save(job);

        return mapToResponse(job);
    }

    @Override
    @Transactional
    public JobResponse completeJob(Long jobId, String notes) {
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId)); // ✅ Sửa
        accessControlService.verifyResourceAccess(job.getTechnician().getId(), "JOB", "COMPLETE");

        if (job.getStartTime() == null)
            throw new CommonException.InvalidOperation("JOB_NOT_STARTED", "Không thể hoàn thành Job chưa bắt đầu"); // ✅ Sửa
        if (job.getActualEndTime() != null)
            throw new CommonException.Conflict("JOB_ALREADY_COMPLETED", "Job đã được hoàn thành"); // ✅ Sửa

        job.setActualEndTime(LocalDateTime.now());
        if (notes != null) job.setNotes(notes);

        job.getBooking().setBookingStatus(BookingStatus.MAINTENANCE_COMPLETE);
        jobRepo.save(job);


        return mapToResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobDetail(Long jobId) {
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId));

        // Check technician trước khi verify access
        if (job.getTechnician() != null) {
            accessControlService.verifyResourceAccess(job.getTechnician().getId(), "JOB", "READ");
        } else {
            // Job chưa assign, chỉ admin/staff mới xem được
            accessControlService.verifyCanAccessAllResources("JOB", "READ");
        }
        return mapToResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getTechnicianTasks(Long technicianId) {
        // ✅ Thêm: Kiểm tra xem technician có tồn tại không
        if (!userRepo.existsById(technicianId)) {
            throw new CommonException.NotFound("Technician (User)", technicianId);
        }

        accessControlService.verifyResourceAccess(technicianId, "JOB", "READ");
        List<Job> jobs = jobRepo.findByTechnicianIdAndNotComplete(technicianId);
        return jobs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        accessControlService.verifyCanAccessAllResources("JOB", "DELETE");
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId)); // ✅ Sửa

        if (job.getStartTime() != null)
            throw new CommonException.Conflict("JOB_ALREADY_STARTED", "Không thể xóa Job đã bắt đầu"); // ✅ Sửa

        jobRepo.deleteById(jobId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getJobsFiltered(
            @Nullable Long technicianId,
            @Nullable JobStatus status,
            @Nullable Long bookingId
    ) {
        accessControlService.verifyCanAccessAllResources("JOB", "READ");

        // ✅ Validate technician nếu có
        if (technicianId != null) {
            if (!userRepo.existsById(technicianId)) {
                throw new CommonException.NotFound("Technician (User)", technicianId);
            }
        }

        // ✅ Validate booking nếu có
        if (bookingId != null) {
            if (!jobRepo.existsByBookingId(bookingId)) {
                throw new CommonException.NotFound("Job cho Booking", bookingId);
            }
        }

        // Lấy jobs theo điều kiện
        List<Job> jobs;

        if (bookingId != null) {
            // Trả về list có 1 phần tử (hoặc empty) vì One-to-One
            Optional<Job> jobOpt = jobRepo.findByBookingId(bookingId);
            jobs = jobOpt.map(List::of).orElse(List.of());
        } else {
            jobs = jobRepo.findAll();
        }

        return jobs.stream()
                // Filter theo technician (AND condition)
                .filter(job -> {
                    if (technicianId == null) return true;
                    return job.getTechnician() != null &&
                           job.getTechnician().getId().equals(technicianId);
                })
                // Filter theo status (AND condition)
                .filter(job -> {
                    if (status == null) return true;

                    // Tính toán status của job
                    JobStatus jobStatus = calculateJobStatus(job);

                    return jobStatus == status;
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianResponse> getAvailableTechnicians(ScheduleDateTime scheduleDateTime) {
        accessControlService.verifyCanAccessAllResources("JOB", "READ");

        // Convert ScheduleDateTime sang LocalDateTime
        LocalDateTime scheduleTime = scheduleDateTime.toLocalDateTime();

        // Lấy tất cả technician ACTIVE
        List<User> allTechnicians = userRepo.findByRoleNameAndStatus("TECHNICIAN", EntityStatus.ACTIVE);

        // Lọc ra những technician rảnh vào khung giờ này và map sang DTO
        return allTechnicians.stream()
                .filter(tech -> isTechnicianAvailableAtTime(tech.getId(), scheduleTime, null))
                .map(this::mapToTechnicianResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTechnicianAvailableAtTime(Long technicianId, LocalDateTime scheduleTime, Long excludeJobId) {
        List<Job> technicianJobs = jobRepo.findByTechnicianIdAndNotComplete(technicianId);
        LocalDateTime now = LocalDateTime.now();

        if(technicianJobs.isEmpty()){
            return true;
        }

        for (Job job : technicianJobs) {
            if (excludeJobId != null && job.getId().equals(excludeJobId)) {
                continue;
            }

            LocalDateTime busyFrom;
            LocalDateTime busyUntil;

            if (job.getStartTime() != null) {
                // --- CASE A: Job STARTED ---
                // Use actual start time.
                busyFrom = job.getStartTime();
                LocalDateTime estimatedEnd = job.getEstEndTime();

                // If running late, extend window to NOW so they don't appear free
                LocalDateTime effectiveEnd = now.isAfter(estimatedEnd) ? now : estimatedEnd;
                busyUntil = effectiveEnd.plusMinutes(JOB_BUFFER_MINUTES);

            } else {
                // --- CASE B: Job ASSIGNED (Future or Overdue) ---
                LocalDateTime scheduled = job.getBooking().getScheduleDate();
                long duration = calculateTotalDuration(job.getBooking());

                LocalDateTime latestAllowedStart = scheduled.plusMinutes(START_WINDOW_MINUTES);

                if (now.isAfter(latestAllowedStart)) {
                    // --- SUB-CASE: OVERDUE GHOST JOB ---
                    // Job should have started but hasn't.
                    // Assume Technician is busy RIGHT NOW waiting to start.
                    busyFrom = now;
                    busyUntil = now.plusMinutes(duration).plusMinutes(JOB_BUFFER_MINUTES);

                } else {
                    // --- SUB-CASE: STANDARD FUTURE JOB ---
                    // Reserve window for Early (-30) and Late (+30) start flexibility
                    busyFrom = scheduled.minusMinutes(START_WINDOW_MINUTES);

                    LocalDateTime worstCaseEnd = scheduled
                            .plusMinutes(START_WINDOW_MINUTES) // Late start possibility
                            .plusMinutes(duration);

                    busyUntil = worstCaseEnd.plusMinutes(JOB_BUFFER_MINUTES);
                }
            }

            // --- Check Intersection ---
            if (!scheduleTime.isBefore(busyFrom) && scheduleTime.isBefore(busyUntil)) {
                return false;
            }
        }

        return true;
    }

    private void validateStartJobTime(Job job, LocalDateTime now) {
        LocalDateTime scheduleTime = job.getBooking().getScheduleDate();

        LocalDateTime earliestStart = scheduleTime.minusMinutes(START_WINDOW_MINUTES);
        LocalDateTime standardLatestStart = scheduleTime.plusMinutes(START_WINDOW_MINUTES);

        // 1. Check: Có sớm quá không?
        if (now.isBefore(earliestStart)) {
            throw new CommonException.InvalidOperation("CANNOT_START_TOO_EARLY",
                    String.format("Chưa đến giờ làm việc. Sớm nhất: %s.", earliestStart.toLocalTime()));
        }

        // 2. Check: Có trễ quá không?
        if (now.isAfter(standardLatestStart)) {
            // Logic "Phao cứu sinh":
            // Nếu quá giờ hẹn, kiểm tra xem Staff có vừa mới gán/update job này không?

            // Lấy thời điểm tương tác cuối cùng (Ưu tiên update, nếu không có thì lấy create)
            LocalDateTime lastInteractionTime = job.getUpdatedAt() != null ? job.getUpdatedAt() : job.getCreatedAt();

            // Deadline ân hạn = Thời điểm update cuối cùng + 30 phút
            LocalDateTime extendedDeadline = lastInteractionTime.plusMinutes(START_WINDOW_MINUTES);

            // Nếu hiện tại còn trễ hơn cả deadline ân hạn -> Lỗi do Technician thật.
            if (now.isAfter(extendedDeadline)) {
                throw new CommonException.InvalidOperation("CANNOT_START_TOO_LATE",
                        String.format("Đã quá hạn bắt đầu (Lịch hẹn: %s). Kể cả tính từ lúc phân công gần nhất (%s), bạn cũng đã trễ.",
                                scheduleTime.toLocalTime(), lastInteractionTime.toLocalTime()));
            }

            // Nếu code chạy xuống đây nghĩa là: Trễ lịch gốc NHƯNG vẫn trong thời gian ân hạn sau khi Staff update.
            // -> Hợp lệ, không throw exception.
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

    private void usePartsForMaintenance(Booking booking) {
        for (BookingDetail detail : booking.getBookingDetails()) {
            Long catalogModelId = detail.getCatalogModel().getId();
            List<MaintenanceCatalogModelPart> requiredParts =
                    maintenanceCatalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId);

            for (MaintenanceCatalogModelPart mp : requiredParts) {
                Part part = mp.getPart();
                BigDecimal qty = mp.getQuantityRequired();

                // 1. Decrease Stock (Available Quantity)
                if (part.getQuantity().compareTo(qty) < 0) {
                    throw new CommonException.InvalidOperation("KHO HẾT HÀNG",
                            "Phụ tùng " + part.getName() + " không đủ trong kho để bắt đầu công việc.");
                }
                part.setQuantity(part.getQuantity().subtract(qty));

                // 2. Decrease Reserved (Since we reserved it at Confirm, we now 'consume' the reservation)
                BigDecimal reservedToDeduct = qty;
                if(part.getReserved().compareTo(qty) < 0) {
                    reservedToDeduct = part.getReserved(); // Deduct whatever is there to avoid negative
                }
                part.setReserved(part.getReserved().subtract(reservedToDeduct));

                // 3. Increase Used Count
                part.setUsed(part.getUsed().add(qty));

                partRepo.save(part);
            }
        }
    }

    private JobStatus calculateJobStatus(Job job) {
        if (job.getActualEndTime() != null) {
            return JobStatus.COMPLETED;
        } else if (job.getStartTime() != null) {
            return JobStatus.IN_PROGRESS;
        } else if (job.getTechnician() == null) {
            return JobStatus.UNASSIGNED;
        } else {
            return JobStatus.PENDING;
        }
    }

    private JobResponse mapToResponse(Job job) {
        JobStatus status = calculateJobStatus(job);

        return JobResponse.builder()
                .id(job.getId())
                .bookingId(job.getBooking() != null ? job.getBooking().getId() : null)
                .technicianId(job.getTechnician() != null ? job.getTechnician().getId() : null)
                .technicianName(job.getTechnician() != null ? job.getTechnician().getFullName() : null)
                .startTime(job.getStartTime())
                .estEndTime(job.getEstEndTime())
                .actualEndTime(job.getActualEndTime())
                .status(status)
                .notes(job.getNotes())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private TechnicianResponse mapToTechnicianResponse(User user) {
        return TechnicianResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .emailAddress(user.getEmailAddress())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole().getDisplayName() : null)
                .status(user.getStatus() != null ? user.getStatus().toString() : null)
                .build();
    }
}
