package com.example.demo.repo;

import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleModelRepo extends JpaRepository<VehicleModel, Long> {
    List<VehicleModel> findByStatus(EntityStatus status);
    List<VehicleModel> findByBrandName(String brandName);
    List<VehicleModel> findByBrandNameAndStatus(String brandName, EntityStatus status);
    Optional<VehicleModel> findByBrandNameAndModelName(String brandName, String modelName);
    boolean existsByBrandNameAndModelName(String brandName, String modelName);

}