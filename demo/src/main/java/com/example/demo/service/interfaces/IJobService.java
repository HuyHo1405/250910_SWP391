package com.example.demo.service.interfaces;

import com.example.demo.model.dto.JobRequest;
import com.example.demo.model.dto.JobResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.dto.TechnicianResponse;
import com.example.demo.model.modelEnum.JobStatus;
import io.micrometer.common.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public interface IJobService {

    JobResponse startJob(Long jobId);

    JobResponse completeJob(Long jobId, String notes);

    JobResponse getJobDetail(Long jobId);

    List<JobResponse> getTechnicianTasks(Long technicianId);

    boolean isTechnicianAvailableAtTime(Long technicianId, LocalDateTime scheduleTime, Long excludeJobId);

    void deleteJob(Long jobId);

    List<JobResponse> getJobsFiltered(
            @Nullable Long technicianId,
            @Nullable JobStatus status,
            @Nullable Long bookingId
    );

    List<TechnicianResponse> getAvailableTechnicians(ScheduleDateTime scheduleDateTime);
}
