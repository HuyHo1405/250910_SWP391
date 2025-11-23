package com.example.demo.service.interfaces;

import com.example.demo.model.dto.FeedbackRequest;
import com.example.demo.model.dto.FeedbackResponse;
import com.example.demo.model.dto.TagResponse;

import java.util.List;

public interface IFeedbackService {
    FeedbackResponse createFeedback(FeedbackRequest feedbackRequest);
    List<TagResponse> getAllTags();
    FeedbackResponse getFeedbackByBookingId(Long bookingId);
    List<FeedbackResponse> getUserFeedbackHistory(Long customerId);
    List<FeedbackResponse> getAllFeedbacks(Long bookingId, Long customerId, Integer rating, Long tagId);
    FeedbackResponse updateFeedback(Long feedbackId, FeedbackRequest feedbackRequest);
    void deleteFeedback(Long feedbackId);
}
