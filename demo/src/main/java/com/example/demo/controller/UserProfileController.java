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
@Tag(name = "User Profile", description = "Endpoints for managing user profiles - Admin, Staff, User")
@RequiredArgsConstructor
public class UserProfileController {

    private final IUserProfileService userProfileService;

    // CREATE - Tạo user mới (admin)
    @PostMapping
    @Operation(summary = "Create new user", description = "Allows an admin to create a new user profile.")
    public ResponseEntity<UserProfileResponse> create(
            @RequestBody UserProfileRequest.CreateProfile request
    ) {
        return ResponseEntity.ok(userProfileService.create(request));
    }

    // READ - Lấy user theo id
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns user profile by user ID. Requires authentication.")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.getById(id));
    }

    // GET ALL - Danh sách user với filter
    @GetMapping
    @Operation(summary = "Get all users with filters", description = "Returns all user profiles filtered by email, name, phone, role, or status. Requires authentication.")
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
    @Operation(summary = "Update user profile info", description = "Allows an authenticated user to update their profile info (email, name, phone, role, status).")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody UserProfileRequest.Profile request
    ) {
        return ResponseEntity.ok(userProfileService.updateProfile(id, request));
    }

    // UPDATE PASSWORD - Đổi mật khẩu (RIÊNG BIỆT)
    @PutMapping("/{id}/password")
    @Operation(summary = "Change user password", description = "Allows an authenticated user to change their password.")
    public ResponseEntity<MessageResponse> updatePassword(
            @PathVariable Long id,
            @RequestBody UserProfileRequest.Password request
    ) {
        return ResponseEntity.ok(userProfileService.updatePassword(id, request));
    }

    // DISABLE - Vô hiệu hóa user
    @PutMapping("/{id}/disable")
    @Operation(summary = "Disable user", description = "Allows an admin to disable a user account.")
    public ResponseEntity<Void> disable(@PathVariable Long id) {
        userProfileService.disable(id);
        return ResponseEntity.noContent().build();
    }

    // REACTIVE - Kích hoạt lại user
    @PutMapping("/{id}/reactive")
    @Operation(summary = "Reactivate user", description = "Allows an admin to reactivate a user account.")
    public ResponseEntity<Void> reactive(@PathVariable Long id) {
        userProfileService.reactive(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE - Xóa user
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Allows an admin to delete a user account.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET ALL ROLES - Lấy danh sách tất cả role display names
    @GetMapping("/roles")
    @Operation(summary = "Get all editable roles for current user (enum schema)", description = "Returns all editable roles for the current user as an enum schema. Requires authentication.")
    public ResponseEntity<EnumSchemaResponse> getAllRoles() {
        return ResponseEntity.ok(userProfileService.getAllRoles());
    }
}
