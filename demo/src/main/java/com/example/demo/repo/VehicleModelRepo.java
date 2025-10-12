package com.example.demo.repo;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleModelRepo extends JpaRepository<VehicleModel, Long> {
    @Query("SELECT v.modelName FROM VehicleModel v WHERE v.status = :status")
    List<String> findModelNamesByStatus(@Param("status") EntityStatus status);
    List<VehicleModel> findByStatus(EntityStatus status);
    List<VehicleModel> findByBrandNameAndStatus(String brandName, EntityStatus status);
    Optional<VehicleModel> findByBrandNameAndModelNameAndStatus(String brandName, String modelName, EntityStatus status);
    boolean existsByBrandNameAndModelNameAndStatus(String brandName, String modelName, EntityStatus status);

    Optional<VehicleModel> findByIdAndStatus(Long id, EntityStatus entityStatus);
}