package com.example.demo.service.interfaces;

import com.example.demo.model.dto.EnumSchemaResponse;
import com.example.demo.model.dto.MessageResponse;
import com.example.demo.model.dto.UserProfileRequest;
import com.example.demo.model.dto.UserProfileResponse;
import com.example.demo.model.modelEnum.EntityStatus;

import java.util.List;

public interface IUserProfileService {
    UserProfileResponse create(UserProfileRequest.CreateProfile request);

    UserProfileResponse getById(Long id);

    List<UserProfileResponse> getAll(
            String email,
            String fullName,
            String phoneNumber,
            String roleDisplayName,
            EntityStatus status
    );

    UserProfileResponse updateProfile(Long id, UserProfileRequest.Profile request);

    MessageResponse updatePassword(Long id, UserProfileRequest.Password request);

    void disable(Long id);

    void reactive(Long id);

    void delete(Long id);

    EnumSchemaResponse getAllRoles();
}