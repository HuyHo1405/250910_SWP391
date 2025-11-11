package com.example.demo.service.interfaces;

import com.example.demo.model.dto.JobRequest;
import com.example.demo.model.dto.JobResponse;
import io.micrometer.common.lang.Nullable;

import java.util.List;

public interface IJobService {
    JobResponse assignJob(JobRequest.JobAssign request);

    JobResponse updateJob(Long jobId, JobRequest.JobUpdate request);

    JobResponse startJob(Long jobId);

    JobResponse completeJob(Long jobId, String notes);

    JobResponse getJobDetail(Long jobId);

    JobResponse getJobsByBookingDetail(Long bookingDetailId);

    List<JobResponse> getTechnicianTasks(Long technicianId);

    void deleteJob(Long jobId);

    /**
     * Lấy danh sách Jobs với filter
     *
     * @param technicianId - ID của kỹ thuật viên (nullable)
     * @param status       - Trạng thái: PENDING, IN_PROGRESS, COMPLETED, UNASSIGNED (nullable)
     * @param bookingId    - ID của booking (nullable)
     * @return Danh sách jobs đã filter
     */
    List<JobResponse> getJobsFiltered(
            @Nullable Long technicianId,
            @Nullable String status,
            @Nullable Long bookingId
    );
}
