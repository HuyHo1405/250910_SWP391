package com.example.demo.controller;

import com.example.demo.model.dto.EnumSchemaResponse;
import com.example.demo.model.dto.MessageResponse;
import com.example.demo.model.dto.UserProfileRequest;
import com.example.demo.model.dto.UserProfileResponse;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.service.interfaces.IUserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userprofile")
@Tag(name = "User Profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final IUserProfileService userProfileService;

    // CREATE - Tạo user mới (admin)
    @PostMapping
    @Operation(summary = "Create new user")
    public ResponseEntity<UserProfileResponse> create(
            @RequestBody UserProfileRequest.CreateProfile request
    ) {
        return ResponseEntity.ok(userProfileService.create(request));
    }

    // READ - Lấy user theo id
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.getById(id));
    }

    // GET ALL - Danh sách user với filter
    @GetMapping
    @Operation(summary = "Get all users with filters")
    public ResponseEntity<List<UserProfileResponse>> getAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String roleDisplayName,
            @RequestParam(required = false) EntityStatus status
    ) {
        return ResponseEntity.ok(
                userProfileService.getAll(email, fullName, phoneNumber, roleDisplayName, status)
        );
    }

    // UPDATE PROFILE - Cập nhật thông tin profile (KHÔNG BAO GỒM PASSWORD)
    @PutMapping("/{id}")
    @Operation(summary = "Update user profile info (email, name, phone, role, status)")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody UserProfileRequest.Profile request
    ) {
        return ResponseEntity.ok(userProfileService.updateProfile(id, request));
    }

    // UPDATE PASSWORD - Đổi mật khẩu (RIÊNG BIỆT)
    @PutMapping("/{id}/password")
    @Operation(summary = "Change user password")
    public ResponseEntity<MessageResponse> updatePassword(
            @PathVariable Long id,
            @RequestBody UserProfileRequest.Password request
    ) {
        return ResponseEntity.ok(userProfileService.updatePassword(id, request));
    }

    // DISABLE - Vô hiệu hóa user
    @PutMapping("/{id}/disable")
    @Operation(summary = "Disable user")
    public ResponseEntity<Void> disable(@PathVariable Long id) {
        userProfileService.disable(id);
        return ResponseEntity.noContent().build();
    }

    // REACTIVE - Kích hoạt lại user
    @PutMapping("/{id}/reactive")
    @Operation(summary = "Reactivate user")
    public ResponseEntity<Void> reactive(@PathVariable Long id) {
        userProfileService.reactive(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE - Xóa user
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET ALL ROLES - Lấy danh sách tất cả role display names
    @GetMapping("/roles")
    @Operation(summary = "Get all editable roles for current user (enum schema)")
    public ResponseEntity<EnumSchemaResponse> getAllRoles() {
        return ResponseEntity.ok(userProfileService.getAllRoles());
    }
}
