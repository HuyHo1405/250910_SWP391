package com.example.demo.service.interfaces;

import com.example.demo.exception.UserException.*;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.impl.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserValidationService userValidationService;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userRepo.findById(id)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(UserNotFound::new);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Check if email or phone already exists
        userValidationService.checkEmailAndPhoneAvailability(
                userDTO.getEmailAddress(),
                userDTO.getPhoneNumber()
        );

        // Map DTO to entity
        User user = modelMapper.map(userDTO, User.class);
//        user.setHashedPassword(passwordEncoder.encode(userDTO.getPassword()));//TODO:
        user.setStatus(EntityStatus.ACTIVE);

        // Set role if provided, otherwise set default role
        if (user.getRole() == null) {
            Role defaultRole = roleRepo.findByName("CUSTOMER")
                    .orElseThrow(RoleNotFound::new);
            user.setRole(defaultRole);
        }

        User savedUser = userRepo.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(UserNotFound::new);

        // Check if email is being changed and if the new email is already in use
        if (!existingUser.getEmailAddress().equals(userDTO.getEmailAddress())) {
            userValidationService.checkEmailAvailability(userDTO.getEmailAddress());
        }

        // Check if phone number is being changed and if the new phone is already in use
        if (!existingUser.getPhoneNumber().equals(userDTO.getPhoneNumber())) {
            userValidationService.checkPhoneAvailability(userDTO.getPhoneNumber());
        }

        // Update fields
        existingUser.setFullName(userDTO.getFullName());
        existingUser.setEmailAddress(userDTO.getEmailAddress());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());

        // Update password if provided
//        existingUser.setHashedPassword(passwordEncoder.encode(userDTO.getPassword())); //TODO:

        // Update role if provided and different
        if (userDTO.getRole() != null && !Objects.equals(existingUser.getRole().getId(), userDTO.getRole().getId())) {
            Role role = roleRepo.findById(userDTO.getRole().getId())
                    .orElseThrow(RoleNotFound::new);
            existingUser.setRole(role);
        }

        User updatedUser = userRepo.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(UserNotFound::new);
        userRepo.delete(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, boolean active) {
        User user = userRepo.findById(id)
                .orElseThrow(UserNotFound::new);
        user.setStatus(active ? EntityStatus.ACTIVE : EntityStatus.ARCHIVED);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void resetUserPassword(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(UserNotFound::new);
        // Generate a random password or set a default one
        String defaultPassword = "P@ssw0rd"; // In production, generate a secure random password
        user.setHashedPassword(passwordEncoder.encode(defaultPassword));
        userRepo.save(user);
        // In a real application, send an email to the user with the new password
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String keyword, Pageable pageable) {
        return userRepo.findByFullNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCaseOrPhoneNumberContaining(
                        keyword, keyword, keyword, pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findByRole(String role, Pageable pageable) {
        return userRepo.findByRoleName(role, pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findByStatus(EntityStatus status, Pageable pageable) {
        return userRepo.findByStatus(status, pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findByRoleAndStatus(String role, EntityStatus status, Pageable pageable) {
        return userRepo.findByRoleNameAndStatus(role, status, pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }
}