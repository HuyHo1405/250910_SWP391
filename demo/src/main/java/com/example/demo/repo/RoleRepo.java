package com.example.demo.repo;

import com.example.demo.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM role_editable WHERE role_id = :roleId AND editable_id = :editableId", nativeQuery = true)
    int canRoleEdit(@Param("roleId") Long roleId, @Param("editableId") Long editableId);

    Optional<Role> findByName(String name);

    Optional<Role> findByDisplayName(String displayName);

    // Lấy danh sách displayName của các role mà STAFF có thể chỉnh sửa
    @Query("SELECT r.displayName FROM Role staff JOIN staff.editableRoles r WHERE staff.id = 2")
    List<String> findStaffEditableRoleDisplayNames();

    // Lấy danh sách displayName của các role mà ADMIN có thể chỉnh sửa
    @Query("SELECT r.displayName FROM Role admin JOIN admin.editableRoles r WHERE admin.id = 1")
    List<String> findAdminEditableRoleDisplayNames();
}
