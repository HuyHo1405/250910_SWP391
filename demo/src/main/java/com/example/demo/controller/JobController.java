package com.example.demo.controller;

import com.example.demo.model.dto.AvailableTechnicianRequest;
import com.example.demo.model.dto.JobRequest;
import com.example.demo.model.dto.JobResponse;
import com.example.demo.model.dto.TechnicianResponse;
import com.example.demo.model.modelEnum.JobStatus;
import com.example.demo.service.interfaces.IJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management")
public class JobController {
    private final IJobService jobService;

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long jobId, @RequestBody JobRequest.JobUpdate request) {
        return ResponseEntity.ok(jobService.updateJob(jobId, request));
    }

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

    @GetMapping("/booking/{bookingId}")
    @Operation(
        summary = "Get job by booking",
        description = "Lấy Job duy nhất của Booking (One-to-One relationship)"
    )
    public ResponseEntity<JobResponse> getJobByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(jobService.getJobByBooking(bookingId));
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

    @PostMapping("/available-technicians")
    @Operation(
        summary = "Get available technicians",
        description = "Lấy danh sách kỹ thuật viên rảnh vào thời điểm được chỉ định. Hỗ trợ nhiều format ngày giờ."
    )
    public ResponseEntity<List<TechnicianResponse>> getAvailableTechnicians(
            @Valid @RequestBody AvailableTechnicianRequest request
    ) {
        return ResponseEntity.ok(
            jobService.getAvailableTechnicians(request.getScheduleDateTime())
        );
    }
}
