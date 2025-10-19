package com.example.demo.repo;

import com.example.demo.model.entity.MaintenanceCatalogModelPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceCatalogModelPartRepo extends JpaRepository<MaintenanceCatalogModelPart, Long> {

    boolean existsByMaintenanceCatalogIdAndVehicleModelIdAndPartId(
            Long catalogId, Long modelId, Long partId);

    Optional<MaintenanceCatalogModelPart> findByMaintenanceCatalogIdAndVehicleModelIdAndPartId(
            Long catalogId, Long modelId, Long partId);

    List<MaintenanceCatalogModelPart> findByMaintenanceCatalogIdAndVehicleModelId(
            Long catalogId, Long modelId);
}
