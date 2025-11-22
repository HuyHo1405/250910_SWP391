package com.example.demo.controller;

import com.example.demo.model.dto.FeedbackRequest;
import com.example.demo.model.dto.FeedbackResponse;
import com.example.demo.model.dto.TagResponse;
import com.example.demo.service.interfaces.IFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedbacks")
public class FeedbackController {
    private final IFeedbackService feedbackService;

    @GetMapping("/tags")
    @Operation(
        summary = "[PRIVATE] [USER] Get all feedback tags",
        description = "Returns all available feedback tags. Accessible to all users."
    )
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(feedbackService.getAllTags());
    }

    @PostMapping
    @Operation(
        summary = "[PRIVATE] [CUSTOMER] Create feedback",
        description = "Allows a logged-in customer to create feedback for a booking."
    )
    public ResponseEntity<FeedbackResponse> createFeedback(@RequestBody @Valid FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.createFeedback(request));
    }

    @GetMapping
    @Operation(
        summary = "[PRIVATE] [STAFF] Get feedbacks with filters",
        description = "Returns feedbacks filtered by booking, customer, rating, or tag. Requires staff/owner authentication."
    )
    public ResponseEntity<List<FeedbackResponse>> getFeedbackWithFilters(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Long tagId
    ) {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks(bookingId, customerId, rating, tagId));
    }

    @PutMapping("/{feedbackId}")
    @Operation(
        summary = "[PRIVATE] [CUSTOMER] Update feedback",
        description = "Allows a logged-in customer to update their feedback."
    )
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody @Valid FeedbackRequest request) {
        // Lưu ý: request.bookingId có thể null hoặc khác, ta ưu tiên lấy theo PathVariable
        return ResponseEntity.ok(feedbackService.updateFeedback(feedbackId, request));
    }

    @DeleteMapping("/{feedbackId}")
    @Operation(
        summary = "[PRIVATE] [CUSTOMER] Delete feedback",
        description = "Allows a logged-in customer to delete their feedback."
    )
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content chuẩn REST
    }
}
