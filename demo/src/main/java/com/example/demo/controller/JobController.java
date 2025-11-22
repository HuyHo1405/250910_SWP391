package com.example.demo.controller;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.JobResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.dto.TechnicianResponse;
import com.example.demo.model.modelEnum.DateTimeFormat;
import com.example.demo.model.modelEnum.JobStatus;
import com.example.demo.service.interfaces.IJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job")
public class JobController {
    private final IJobService jobService;

    @PutMapping("/{jobId}/start")
    @Operation(summary = "[PRIVATE] [TECHNICIAN/STAFF] Start job", description = "Allows a technician or staff to start a job by its id.")
    public ResponseEntity<JobResponse> startJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.startJob(jobId));
    }

    @PutMapping("/{jobId}/complete")
    @Operation(summary = "[PRIVATE] [TECHNICIAN/STAFF] Complete job", description = "Allows a technician or staff to complete a job by its id, with optional notes.")
    public ResponseEntity<JobResponse> completeJob(@PathVariable Long jobId, @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(jobService.completeJob(jobId, notes));
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "[PRIVATE] [OWNER/STAFF] Get job detail", description = "Returns job details by job id. Requires authentication.")
    public ResponseEntity<JobResponse> getJobDetail(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJobDetail(jobId));
    }

    @GetMapping("/technician/{technicianId}/tasks")
    @Operation(summary = "[PRIVATE] [TECHNICIAN/STAFF] Get technician tasks", description = "Returns all jobs assigned to a technician.")
    public ResponseEntity<List<JobResponse>> getTechnicianTasks(@PathVariable Long technicianId) {
        return ResponseEntity.ok(jobService.getTechnicianTasks(technicianId));
    }

    @DeleteMapping("/{jobId}")
    @Operation(summary = "[PRIVATE] [STAFF] Delete job", description = "Allows staff to delete a job by its id.")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "[PRIVATE] [STAFF] Get jobs filtered", description = "Returns jobs filtered by technician, status, or booking id. Requires authentication.")
    public ResponseEntity<List<JobResponse>> getJobsFiltered(
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) Long bookingId
    ) {
        return ResponseEntity.ok(jobService.getJobsFiltered(technicianId, status, bookingId));
    }

    @GetMapping("/available-technicians")
    @Operation(
        summary = "[PRIVATE] [STAFF] Get available technicians",
        description = "Returns a list of available technicians for a given schedule time. Format: iso, timestamp, or custom. Example: scheduleTime=2025-11-13T09:00:00&format=iso or scheduleTime=1699704600000&format=timestamp. Requires staff authentication."
    )
    public ResponseEntity<List<TechnicianResponse>> getAvailableTechnicians(
            @RequestParam String scheduleTime,
            @RequestParam(defaultValue = "iso") DateTimeFormat format,
            @RequestParam(required = false, defaultValue = "UTC") String timezone
    ) {
        ScheduleDateTime scheduleDateTime = ScheduleDateTime.builder()
                .format(format.getValue())
                .value(scheduleTime)
                .timezone(timezone)
                .build();

        // Validate the ScheduleDateTime object
        if (!scheduleDateTime.isValid()) {
            throw new CommonException.InvalidOperation("Định dạng hoặc giá trị ngày/giờ không hợp lệ");
        }

        if (!scheduleDateTime.isHourOnly()) {
            throw new CommonException.InvalidOperation("Giờ hẹn chỉ được chọn theo giờ tròn (phút và giây phải là 00)");
        }

        return ResponseEntity.ok(
            jobService.getAvailableTechnicians(scheduleDateTime)
        );
    }
}
