package com.example.demo.service.impl;

import com.example.demo.exception.UserException;
import com.example.demo.model.dto.MessageResponse;
import com.example.demo.model.dto.UserProfileRequest;
import com.example.demo.model.dto.UserProfileResponse;
import com.example.demo.model.entity.Role;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.entity.User;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.interfaces.IMailService;
import com.example.demo.service.interfaces.IPasswordService;
import com.example.demo.service.interfaces.IUserProfileService;
import com.example.demo.exception.CommonException;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService implements IUserProfileService {

    private final UserContextService userContextService;
    private final UserValidationService userValidationService;
    private final AccessControlService accessControlService;
    private final IPasswordService passwordService;
    private final IMailService mailService;

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse create(UserProfileRequest.Profile request) {

        accessControlService.verifyResourceAccessWithoutOwnership("USER", "CREATE");

        userValidationService.checkEmailAndPhoneAvailability(request.getEmail(), request.getPhoneNumber());

        User user = new User();
        user.setEmailAddress(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus(EntityStatus.ACTIVE);

        // Gen password and send mail
        String genPassword = passwordService.generatePassword();
        user.setHashedPassword(passwordEncoder.encode(genPassword));
        mailService.sendGeneratedPassword(request.getEmail(), genPassword);

        // Xử lý role: nullable, mặc định CUSTOMER
        String roleDisplayName = (request.getRoleDisplayName() != null) ? request.getRoleDisplayName() : "Customer";

        Role role = roleRepo.findByDisplayName(roleDisplayName)
                .orElseThrow(() -> new CommonException.NotFound("Role Display Name", roleDisplayName));

        userContextService.checkRoleEditable(role.getId());

        user.setRole(role);

        user = userRepo.save(user);
        return toDto(user);
    }

    @Override
    public UserProfileResponse getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("User", id));

        accessControlService.verifyResourceAccess(user.getId(), "USER", "read");

        boolean checkStaffAccess =
                userContextService.isStaff() &&
                !accessControlService.isResourceOwner(user.getId());

        if(checkStaffAccess) {
            userContextService.checkRoleEditable(user.getRole());
        }
        return toDto(user);
    }

    @Override
    public List<UserProfileResponse> getAll(
            @Nullable String email,
            @Nullable String fullName,
            @Nullable String phoneNumber,
            @Nullable String roleDisplayName,
            @Nullable EntityStatus status) {
        accessControlService.verifyCanAccessAllResources("USER", "read");

        List<User> users;

        if(userContextService.isStaff()){
            if(roleDisplayName != null){
                userContextService.checkRoleEditable(roleDisplayName);
            }
            users = userRepo.findWithStaffFilters(email, fullName, phoneNumber, roleDisplayName, status != null? status.name(): null);
        } else {
            users = userRepo.findWithFilters(email, fullName, phoneNumber, roleDisplayName, status != null? status.name(): null);
        }

        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public UserProfileResponse updateProfile(Long id, UserProfileRequest.Profile request) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("User", id));

        accessControlService.verifyResourceAccess(user.getId(), "USER", "update");

        user.setEmailAddress(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        // Xử lý role nếu truyền lên, nếu không thì giữ nguyên
        if(request.getRoleDisplayName() != null) {
            String roleDisplayName = request.getRoleDisplayName();

            Role role = roleRepo.findByDisplayName(roleDisplayName)
                    .orElseThrow(() -> new UserException.InvalidRoleTransition("CURRENT", roleDisplayName));

            userContextService.checkRoleEditable(role.getId());
            user.setRole(role);
        }

        user = userRepo.save(user);
        return toDto(user);
    }

    @Override
    public MessageResponse updatePassword(Long id, UserProfileRequest.Password request){
        return toDto(passwordService.updatePassword(id, request));
    }

    @Override
    public void disable(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("User", id));
        accessControlService.verifyResourceAccess(user.getId(), "USER", "disable");
        userContextService.checkRoleEditable(user.getRole());
        user.setStatus(EntityStatus.INACTIVE);
        userRepo.save(user);
    }

    @Override
    public void reactive(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("User", id));
        accessControlService.verifyResourceAccess(user.getId(), "USER", "reactive");
        userContextService.checkRoleEditable(user.getRole());
        user.setStatus(EntityStatus.ACTIVE);
        userRepo.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("User", id));
        accessControlService.verifyResourceAccess(user.getId(), "USER", "delete");
        userContextService.checkRoleEditable(user.getRole());
        userRepo.delete(user);
    }

    private UserProfileResponse toDto(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmailAddress())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .roleDisplayName(user.getRole().getDisplayName())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLoginAt())
                .build();
    }

    private MessageResponse toDto(String message){
        return MessageResponse.builder()
                .message(message)
                .build();
    }

}
