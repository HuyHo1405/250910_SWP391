package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.JobRequest;
import com.example.demo.model.dto.JobResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.dto.TechnicianResponse;
import com.example.demo.model.entity.BookingDetail;
import com.example.demo.model.entity.Job;
import com.example.demo.model.entity.MaintenanceCatalogModel;
import com.example.demo.model.entity.User;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.JobStatus;
import com.example.demo.repo.BookingDetailRepo;
import com.example.demo.repo.JobRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.interfaces.IJobService;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final AccessControlService accessControlService;

    private final JobRepo jobRepo;
    private final BookingDetailRepo bookingDetailRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, JobRequest.JobUpdate request) {
        accessControlService.verifyCanAccessAllResources("JOB", "UPDATE");

        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId));

        // chỉ cho update khi chưa start
        if (job.getStartTime() != null)
            throw new CommonException.Conflict("JOB_ALREADY_STARTED", "Không thể cập nhật Job đã bắt đầu");

        if (request.getBookingDetailId() != null) {
            if(job.getBookingDetail().getId() != null && !job.getBookingDetail().getId().equals(request.getBookingDetailId())){
                throw new CommonException.InvalidOperation("CANNOT_CHANGE_BOOKING_DETAIL", "Không thể thay đổi BookingDetail của Job đã được gán");
            }

            BookingDetail bd = bookingDetailRepo.findById(request.getBookingDetailId())
                    .orElseThrow(() -> new CommonException.NotFound("BookingDetail", request.getBookingDetailId()));

            Job check = jobRepo.findByBookingDetailId(request.getBookingDetailId()).orElse(null);
            // Đảm bảo nó không báo lỗi "đã tồn tại" với chính nó
            if (check != null && !check.getId().equals(jobId)) {
                throw new CommonException.AlreadyExists("Job", "BookingDetailId", request.getBookingDetailId());
            }

            checkBookingInProgress(request.getBookingDetailId());
            job.setBookingDetail(bd);
        }

        if (request.getTechnicianId() != null) {
            User technician = userRepo.findById(request.getTechnicianId())
                    .orElseThrow(() -> new CommonException.NotFound("Technician (User)", request.getTechnicianId()));
            if(!technician.getRole().getName().equals("TECHNICIAN"))
                throw new CommonException.InvalidOperation("INVALID_ROLE", "Người dùng được gán phải có vai trò là kỹ thuật viên");

            // ← THÊM MỚI: Kiểm tra technician có rảnh không vào giờ của booking
            checkTechnicianAvailability(request.getTechnicianId(), job.getBookingDetail().getBooking().getScheduleDate(), jobId);

            job.setTechnician(technician);
        }

        if (request.getNotes() != null) job.setNotes(request.getNotes());

        jobRepo.save(job);
        return mapToResponse(job);
    }

    @Override
    @Transactional
    public JobResponse startJob(Long jobId) {
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId)); // ✅ Sửa
        accessControlService.verifyResourceAccess(job.getTechnician().getId(), "JOB", "START");

        if (job.getStartTime() != null)
            throw new CommonException.Conflict("JOB_ALREADY_STARTED", "Job đã được bắt đầu"); // ✅ Sửa

        MaintenanceCatalogModel catalogModel = job.getBookingDetail().getCatalogModel();
        Double estTime = catalogModel.getEstTimeMinutes();

        job.setStartTime(LocalDateTime.now());
        job.setEstEndTime(LocalDateTime.now().plusMinutes(estTime.longValue()));

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
        jobRepo.save(job);
        return mapToResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobDetail(Long jobId) {
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId));

        // ← SỬA: Check technician trước khi verify access
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
    public JobResponse getJobsByBookingDetail(Long bookingDetailId) {
        Job job = jobRepo.findByBookingDetailId(bookingDetailId)
                .orElseThrow(() -> new CommonException.NotFound("Job cho BookingDetailId", bookingDetailId));

        // ← SỬA: Check technician trước khi verify access
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
            @Nullable JobStatus status, // ← ĐỔI: String → JobStatus enum
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
                throw new CommonException.NotFound("Booking", bookingId);
            }
        }

        // Lấy jobs theo điều kiện
        List<Job> jobs;

        if (bookingId != null) {
            jobs = jobRepo.findByBookingId(bookingId);
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

    private void checkBookingInProgress(Long bookingDetailsId){
        BookingDetail bd = bookingDetailRepo.findById(bookingDetailsId)
                .orElseThrow(() -> new CommonException.NotFound("BookingDetail", bookingDetailsId)); // ✅ Sửa

        if(bd.getBooking().getBookingStatus() != BookingStatus.IN_PROGRESS){
            throw new CommonException.InvalidOperation("BOOKING_NOT_IN_PROGRESS", "Booking không ở trạng thái IN_PROGRESS"); // ✅ Sửa
        }
    }

    /**
     * Kiểm tra technician có rảnh vào thời điểm scheduleTime không
     * @param technicianId ID của technician
     * @param scheduleTime Thời điểm cần kiểm tra
     * @param excludeJobId Job ID cần loại trừ (dùng khi update job)
     * @throws CommonException.InvalidOperation nếu technician không rảnh
     */
    private void checkTechnicianAvailability(Long technicianId, LocalDateTime scheduleTime, Long excludeJobId) {
        if (!isTechnicianAvailableAtTime(technicianId, scheduleTime, excludeJobId)) {
            throw new CommonException.InvalidOperation(
                String.format("Kỹ thuật viên không rảnh vào thời điểm %s. Vui lòng chọn người khác hoặc th��i gian khác.",
                    scheduleTime)
            );
        }
    }

    /**
     * Kiểm tra technician có rảnh vào thời điểm scheduleTime không
     * @param technicianId ID của technician
     * @param scheduleTime Thời điểm cần kiểm tra (giờ hẹn của booking)
     * @param excludeJobId Job ID cần loại trừ khi update (nullable)
     * @return true nếu rảnh, false nếu bận
     */
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
            LocalDateTime jobScheduleTime = job.getBookingDetail().getBooking().getScheduleDate();

            // Kiểm tra trùng giờ (cùng giờ tròn)
            // VD: 09:00:00 == 09:00:00
            if (jobScheduleTime.withMinute(0).withSecond(0).equals(scheduleTime.withMinute(0).withSecond(0))) {
                return false; // Technician bận vào giờ này
            }
        }

        return true; // Technician rảnh
    }

    /**
     * Tính toán status của job dựa trên các field
     */
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

    // ... (mapToResponse không đổi) ...
    private JobResponse mapToResponse(Job job) {
        JobStatus status = calculateJobStatus(job);

        return JobResponse.builder()
                .id(job.getId())
                .description(job.getBookingDetail() != null ? job.getBookingDetail().getDescription() : null)
                .bookingDetailId(job.getBookingDetail() != null ? job.getBookingDetail().getId() : null)
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

    /**
     * Map User entity sang TechnicianResponse DTO
     */
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
