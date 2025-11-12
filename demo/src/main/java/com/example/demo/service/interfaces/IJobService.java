package com.example.demo.service.interfaces;

import com.example.demo.model.dto.JobRequest;
import com.example.demo.model.dto.JobResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.dto.TechnicianResponse;
import com.example.demo.model.modelEnum.JobStatus;
import io.micrometer.common.lang.Nullable;

import java.util.List;

public interface IJobService {

    JobResponse updateJob(Long jobId, JobRequest.JobUpdate request);

    JobResponse startJob(Long jobId);

    JobResponse completeJob(Long jobId, String notes);

    JobResponse getJobDetail(Long jobId);

    /**
     * Lấy Job duy nhất của Booking (One-to-One relationship)
     *
     * @param bookingId - ID của booking
     * @return JobResponse
     */
    JobResponse getJobByBooking(Long bookingId);

    List<JobResponse> getTechnicianTasks(Long technicianId);

    void deleteJob(Long jobId);

    /**
     * Lấy danh sách Jobs với filter
     *
     * @param technicianId - ID của kỹ thuật viên (nullable)
     * @param status       - JobStatus enum: UNASSIGNED, PENDING, IN_PROGRESS, COMPLETED (nullable)
     * @param bookingId    - ID của booking (nullable)
     * @return Danh sách jobs đã filter
     */
    List<JobResponse> getJobsFiltered(
            @Nullable Long technicianId,
            @Nullable JobStatus status,
            @Nullable Long bookingId
    );

    /**
     * Lấy danh sách technician rảnh vào thời điểm scheduleDateTime
     *
     * @param scheduleDateTime - Thời điểm cần kiểm tra (ScheduleDateTime object hỗ trợ nhiều format)
     * @return Danh sách TechnicianResponse (DTO) có rảnh vào giờ đó
     */
    List<TechnicianResponse> getAvailableTechnicians(ScheduleDateTime scheduleDateTime);
}
