package com.example.demo.controller;

import com.example.demo.model.dto.JobRequest;
import com.example.demo.model.dto.JobResponse;
import com.example.demo.service.interfaces.IJobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/booking-detail/{bookingDetailId}")
    public ResponseEntity<JobResponse> getJobsByBookingDetail(@PathVariable Long bookingDetailId) {
        return ResponseEntity.ok(jobService.getJobsByBookingDetail(bookingDetailId));
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
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long bookingId
    ) {
        return ResponseEntity.ok(jobService.getJobsFiltered(technicianId, status, bookingId));
    }
}
