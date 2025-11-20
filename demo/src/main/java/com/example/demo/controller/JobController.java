package com.example.demo.controller;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.JobRequest;
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
    public ResponseEntity<JobResponse> startJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.startJob(jobId));
    }

    @PutMapping("/{jobId}/complete")
    public ResponseEntity<JobResponse> completeJob(@PathVariable Long jobId, @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(jobService.completeJob(jobId, notes));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJobDetail(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJobDetail(jobId));
    }

    @GetMapping("/technician/{technicianId}/tasks")
    public ResponseEntity<List<JobResponse>> getTechnicianTasks(@PathVariable Long technicianId) {
        return ResponseEntity.ok(jobService.getTechnicianTasks(technicianId));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getJobsFiltered(
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) Long bookingId
    ) {
        return ResponseEntity.ok(jobService.getJobsFiltered(technicianId, status, bookingId));
    }

    @GetMapping("/available-technicians")
    @Operation(
        summary = "Get available technicians",
        description = "Lấy danh sách kỹ thuật viên rảnh vào thời điểm được chỉ định. " +
                      "Format: iso, timestamp, hoặc custom. " +
                      "Ví dụ: scheduleTime=2025-11-13T09:00:00&format=iso hoặc scheduleTime=1699704600000&format=timestamp"
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
