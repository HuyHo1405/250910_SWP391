package com.example.demo.repo;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.entity.User;
import org.springframework.data.domain.Page;  // ✔ đúng
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress(String email);

    Optional<User> findByPhoneNumber(String emailAddress);


    @Query("SELECT u FROM User u WHERE " +
            "(:emailAddress IS NULL OR u.emailAddress LIKE %:emailAddress%) AND " +
            "(:fullName IS NULL OR u.fullName LIKE %:fullName%) AND " +
            "(:phoneNumber IS NULL OR u.phoneNumber LIKE %:phoneNumber%) AND " +
            "(:roleDisplayName IS NULL OR u.role.displayName = :roleDisplayName) AND " +
            "(u.role.displayName != 'Admin') AND " +
            "(:status IS NULL OR u.status = :status)")
    List<User> findWithFilters(
            @Param("emailAddress") String emailAddress,
            @Param("fullName") String fullName,
            @Param("phoneNumber") String phoneNumber,
            @Param("roleDisplayName") String roleDisplayName,
            @Param("status") String status
    );

    @Query("SELECT u FROM User u WHERE " +
            "(:emailAddress IS NULL OR u.emailAddress LIKE %:emailAddress%) AND " +
            "(:fullName IS NULL OR u.fullName LIKE %:fullName%) AND " +
            "(:phoneNumber IS NULL OR u.phoneNumber LIKE %:phoneNumber%) AND " +
            "(:roleDisplayName IS NULL OR u.role.displayName = :roleDisplayName) AND " +
            "(u.role.displayName != 'Admin') AND " +
            "(u.role.displayName != 'Staff Employee') AND " +
            "(:status IS NULL OR STR(u.status) = STR(:status))")
    List<User> findWithStaffFilters(
            @Param("emailAddress") String emailAddress,
            @Param("fullName") String fullName,
            @Param("phoneNumber") String phoneNumber,
            @Param("roleDisplayName") String roleDisplayName,
            @Param("status") String status
    );

    Page<User> findByRoleName(String role, Pageable pageable);

    Page<User> findByStatus(EntityStatus status, Pageable pageable);

    Page<User> findByRoleNameAndStatus(String role, EntityStatus status, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCaseOrPhoneNumberContaining(String keyword, String keyword1, String keyword2, Pageable pageable);

    /**
     * Lấy danh sách technician theo role name và status
     */
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.status = :status")
    List<User> findByRoleNameAndStatus(@Param("roleName") String roleName, @Param("status") EntityStatus status);
}
