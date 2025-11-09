package com.example.demo.service.interfaces;

import com.example.demo.model.dto.JobRequest;
import com.example.demo.model.dto.JobResponse;

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

    // Lấy danh sách các Job chưa được assign technician
    List<JobResponse> getUnassignedJobs();
}
