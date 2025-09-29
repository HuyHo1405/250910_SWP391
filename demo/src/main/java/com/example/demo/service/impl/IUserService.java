package com.example.demo.service.impl;

import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    Page<UserDTO> getAllUsers(Pageable pageable);

    UserDTO getUserById(Long id);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    void updateUserStatus(Long id, boolean active);

    void resetUserPassword(Long userId);

    Page<UserDTO> searchUsers(String keyword, Pageable pageable);

    Page<UserDTO> findByRole(String role, Pageable pageable);

    Page<UserDTO> findByStatus(EntityStatus status, Pageable pageable);

    Page<UserDTO> findByRoleAndStatus(String role, EntityStatus status, Pageable pageable);

}
