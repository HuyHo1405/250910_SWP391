package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.FeedbackRequest;
import com.example.demo.model.dto.FeedbackResponse;
import com.example.demo.model.dto.TagResponse;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.Feedback;
import com.example.demo.model.entity.FeedbackTag;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.FeedbackRepo;
import com.example.demo.repo.TagRepo;
import com.example.demo.service.interfaces.IFeedbackService;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService implements IFeedbackService {

    private final AccessControlService accessControlService;
    private final FeedbackRepo feedbackRepo;
    private final BookingRepo bookingRepo;
    private final TagRepo tagRepo;

    @Override
    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        // 1. Tìm Booking -> Ném NotFound 404 nếu không thấy
        Booking booking = bookingRepo.findById(request.getBookingId())
                .orElseThrow(() -> new CommonException.NotFound("Booking", request.getBookingId()));

        // 2. Check quyền chủ sở hữu
        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "read");

        // 3. Check trạng thái -> Ném InvalidOperation 400 (Lỗi logic quy trình)
        if (booking.getBookingStatus() != BookingStatus.MAINTENANCE_COMPLETE) {
            throw new CommonException.InvalidOperation("Chỉ được phép đánh giá khi đơn bảo dưỡng đã hoàn thành.");
        }

        // 4. Check trùng lặp -> Ném AlreadyExists 409 (Conflict)
        // Message tự sinh: "Feedback đã tồn tại với bookingId: 123"
        if (feedbackRepo.findByBookingId(request.getBookingId()).isPresent()) {
            throw new CommonException.AlreadyExists("Feedback", "bookingId", request.getBookingId());
        }

        // 5. Xử lý Tags
        Set<FeedbackTag> tags = validateAndGetTags(request.getRating(), request.getTagIds());

        // 6. Save
        Feedback feedback = Feedback.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .booking(booking)
                .customer(booking.getCustomer())
                .feedbackTags(tags)
                .build();

        return mapToResponse(feedbackRepo.save(feedback));
    }


    @Override
    @Transactional
    public FeedbackResponse updateFeedback(Long feedbackId, FeedbackRequest request) {
        // 1. Tìm Feedback cũ (Ném 404 nếu không thấy)
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new CommonException.NotFound("Feedback", feedbackId));

        // 2. SECURITY CHECK: Chỉ chủ sở hữu mới được sửa
        // (Lấy ID khách hàng từ feedback cũ để so sánh với người đang login)
        accessControlService.verifyResourceAccess(feedback.getCustomer().getId(), "FEEDBACK", "update");

        // 3. Business Logic Check
         if (!feedback.getBooking().getId().equals(request.getBookingId())) {
             throw new CommonException.InvalidOperation("Không được phép thay đổi booking của feedback.");
         }

        // 4. XỬ LÝ TAGS (QUAN TRỌNG)
        // Gọi hàm helper để lấy danh sách tag mới (đã validate rating)
        Set<FeedbackTag> newTags = validateAndGetTags(request.getRating(), request.getTagIds());

        // 5. Cập nhật thông tin cơ bản
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());

        // 6. CẬP NHẬT MAPPING (CLEAR & REPLACE)
        // Bước này giúp JPA tự động xóa các dòng cũ trong bảng trung gian
        // và insert các dòng mới vào. Không bị rác dữ liệu.
        feedback.getFeedbackTags().clear();
        feedback.getFeedbackTags().addAll(newTags);

        // 7. Save & Return
        return mapToResponse(feedbackRepo.save(feedback));
    }

    @Override
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        // 1. Tìm Feedback
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new CommonException.NotFound("Feedback", feedbackId));

        // 2. SECURITY CHECK: Chủ sở hữu HOẶC Admin/Manager được quyền xóa
        // (Tùy policy của bạn, ở đây mình dùng quyền 'delete' chung)
        accessControlService.verifyResourceAccess(feedback.getCustomer().getId(), "FEEDBACK", "delete");

        // 3. Delete
        // JPA sẽ xóa dòng trong bảng 'feedbacks'
        // VÀ tự động xóa các dòng liên kết trong bảng 'feedback_tag_mapping'.
        // (Nó KHÔNG xóa Booking hay User, yên tâm).
        feedbackRepo.delete(feedback);
    }

    @Override
    public List<TagResponse> getAllTags() {
        accessControlService.verifyResourceAccessWithoutOwnership("FEEDBACK", "read");

        return tagRepo.findAll().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .content(tag.getContent())
                        .ratingTarget(tag.getTargetRating())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getAllFeedbacks(
            @Nullable Long bookingId,
            @Nullable Long customerId,
            @Nullable Integer rating,
            @Nullable Long tagId
    ) {
        accessControlService.verifyCanAccessAllResources("FEEDBACK", "read");

        // 2. Gọi Repo (Truyền null thoải mái)
        List<Feedback> feedbacks = feedbackRepo.findWithFilters(
                bookingId,
                customerId,
                rating,
                tagId
        );

        // 3. Map sang DTO
        return feedbacks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Set<FeedbackTag> validateAndGetTags(Integer rating, Set<Long> tagIds) {
        // Nếu không gửi tag gì -> Trả về rỗng (hợp lệ)
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }

        // Lọc trùng ID đầu vào
        Set<Long> uniqueTagIds = new HashSet<>(tagIds);

        // Query DB 1 lần
        List<FeedbackTag> foundTags = tagRepo.findAllById(uniqueTagIds);

        // Check ID ảo (Gửi 5 ID mà tìm được có 3 -> Lỗi)
        if (foundTags.size() != uniqueTagIds.size()) {
            throw new CommonException.BadRequest("Danh sách Tag chứa ID không tồn tại trong hệ thống.");
        }

        // Check Logic Rating (Tag của sao nào đi với sao đó)
        Optional<FeedbackTag> invalidTag = foundTags.stream()
                .filter(tag -> !tag.getTargetRating().equals(rating))
                .findFirst();

        if (invalidTag.isPresent()) {
            FeedbackTag tag = invalidTag.get();
            throw new CommonException.InvalidOperation(
                    String.format("Tag '%s' (dành cho %d sao) không hợp lệ với đánh giá %d sao.",
                            tag.getContent(), tag.getTargetRating(), rating)
            );
        }

        return new HashSet<>(foundTags);
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        Set<TagResponse> tagDtos = feedback.getFeedbackTags().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .content(tag.getContent())
                        .ratingTarget(tag.getTargetRating())
                        .build())
                .collect(Collectors.toSet());

        return FeedbackResponse.builder()
                .id(feedback.getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .tags(tagDtos)
                .bookingId(feedback.getBooking().getId())
                .customerId(feedback.getCustomer().getId())
                .customerName(feedback.getCustomer().getFullName())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}