package com.example.demo.service.impl;

import com.example.demo.exception.CommonException; // ✅ Import
import com.example.demo.model.dto.JobRequest;
import com.example.demo.model.dto.JobResponse;
import com.example.demo.model.entity.BookingDetail;
import com.example.demo.model.entity.Job;
import com.example.demo.model.entity.MaintenanceCatalogModel;
import com.example.demo.model.entity.User;
import com.example.demo.model.modelEnum.BookingStatus;
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

        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId)); // ✅ Sửa

        // chỉ cho update khi chưa start
        if (job.getStartTime() != null)
            throw new CommonException.Conflict("JOB_ALREADY_STARTED", "Không thể cập nhật Job đã bắt đầu"); // ✅ Sửa

        if (request.getBookingDetailId() != null) {
            BookingDetail bd = bookingDetailRepo.findById(request.getBookingDetailId())
                    .orElseThrow(() -> new CommonException.NotFound("BookingDetail", request.getBookingDetailId())); // ✅ Sửa

            Job check = jobRepo.findByBookingDetailId(request.getBookingDetailId()).orElse(null);
            // Đảm bảo nó không báo lỗi "đã tồn tại" với chính nó
            if (check != null && !check.getId().equals(jobId)) {
                throw new CommonException.AlreadyExists("Job", "BookingDetailId", request.getBookingDetailId()); // ✅ Sửa
            }

            checkBookingInProgress(request.getBookingDetailId());
            job.setBookingDetail(bd);
        }

        if (request.getTechnicianId() != null) {
            User technician = userRepo.findById(request.getTechnicianId())
                    .orElseThrow(() -> new CommonException.NotFound("Technician (User)", request.getTechnicianId())); // ✅ Sửa
            if(!technician.getRole().getName().equals("TECHNICIAN"))
                throw new CommonException.InvalidOperation("INVALID_ROLE", "Người dùng được gán phải có vai trò là kỹ thuật viên");
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
        Job job = jobRepo.findById(jobId).orElseThrow(() -> new CommonException.NotFound("Job", jobId)); // ✅ Sửa
        accessControlService.verifyResourceAccess(job.getTechnician().getId(), "JOB", "READ");
        return mapToResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobsByBookingDetail(Long bookingDetailId) {
        Job job = jobRepo.findByBookingDetailId(bookingDetailId)
                .orElseThrow(() -> new CommonException.NotFound("Job cho BookingDetailId", bookingDetailId)); // ✅ Sửa
        accessControlService.verifyResourceAccess(job.getTechnician().getId(), "JOB", "READ");
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
            @Nullable String status, // "PENDING", "IN_PROGRESS", "COMPLETED", "UNASSIGNED"
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
            if (!bookingDetailRepo.existsByBookingId(bookingId)) {
                throw new CommonException.NotFound("Booking", bookingId);
            }
        }

        // Lấy tất cả jobs
        List<Job> allJobs = jobRepo.findAll();

        return allJobs.stream()
                // Filter theo technician
                .filter(job -> {
                    if (technicianId == null) return true;
                    return job.getTechnician() != null &&
                           job.getTechnician().getId().equals(technicianId);
                })
                // Filter theo status
                .filter(job -> {
                    if (status == null) return true;

                    String jobStatus;
                    if (job.getActualEndTime() != null) {
                        jobStatus = "COMPLETED";
                    } else if (job.getStartTime() != null) {
                        jobStatus = "IN_PROGRESS";
                    } else if (job.getTechnician() == null) {
                        jobStatus = "UNASSIGNED";
                    } else {
                        jobStatus = "PENDING";
                    }

                    return jobStatus.equalsIgnoreCase(status);
                })
                // Filter theo booking
                .filter(job -> {
                    if (bookingId == null) return true;
                    return job.getBookingDetail() != null &&
                           job.getBookingDetail().getBooking().getId().equals(bookingId);
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void checkBookingInProgress(Long bookingDetailsId){
        BookingDetail bd = bookingDetailRepo.findById(bookingDetailsId)
                .orElseThrow(() -> new CommonException.NotFound("BookingDetail", bookingDetailsId)); // ✅ Sửa

        if(bd.getBooking().getBookingStatus() != BookingStatus.IN_PROGRESS){
            throw new CommonException.InvalidOperation("BOOKING_NOT_IN_PROGRESS", "Booking không ở trạng thái IN_PROGRESS"); // ✅ Sửa
        }
    }

    // ... (mapToResponse không đổi) ...
    private JobResponse mapToResponse(Job job) {
        String status;
        if (job.getActualEndTime() != null) status = "COMPLETED";
        else if (job.getStartTime() != null) status = "IN_PROGRESS";
        else status = "PENDING";

        return JobResponse.builder()
                .id(job.getId())
                .description(job.getBookingDetail() != null ? job.getBookingDetail().getDescription() : null)
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
}

