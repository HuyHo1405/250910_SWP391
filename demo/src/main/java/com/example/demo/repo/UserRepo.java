package com.example.demo.repo;

import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.User;
import org.springframework.data.domain.Page;  // ✔ đúng
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress(String email);

    Optional<User> findByPhoneNumber(String emailAddress);


    Page<User> findByRoleName(String role, Pageable pageable);

    Page<User> findByStatus(EntityStatus status, Pageable pageable);

    Page<User> findByRoleNameAndStatus(String role, EntityStatus status, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCaseOrPhoneNumberContaining(String keyword, String keyword1, String keyword2, Pageable pageable);
}
