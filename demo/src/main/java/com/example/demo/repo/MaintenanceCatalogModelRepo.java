package com.example.demo.repo;

import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.entity.MaintenanceCatalogModel;
import com.example.demo.model.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceCatalogModelRepo extends JpaRepository<MaintenanceCatalogModel, Long> {

    boolean existsByMaintenanceCatalogIdAndVehicleModelId(Long maintenanceCatalogId, Long vehicleModelId);

    List<MaintenanceCatalogModel> findByMaintenanceCatalogId(Long catalogId);

    List<MaintenanceCatalogModel> findByMaintenanceCatalogIdAndVehicleModelId(Long catalogId, Long modelId);

    Optional<MaintenanceCatalogModel> findFirstByMaintenanceCatalogIdAndVehicleModelId(Long catalogId, Long modelId);

}
