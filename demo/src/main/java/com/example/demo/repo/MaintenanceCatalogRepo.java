package com.example.demo.repo;

import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceCatalogRepo extends JpaRepository<MaintenanceCatalog, Long> {

    @Query("SELECT DISTINCT mc FROM MaintenanceCatalog mc " +
            "JOIN mc.models mcm " +
            "JOIN mcm.vehicleModel vm " +
            "JOIN Vehicle v ON v.model.id = vm.id " +
            "WHERE (:type IS NULL OR mc.maintenanceServiceType = :type) " +
            "AND (:vin IS NULL OR v.vin = :vin)")
    List<MaintenanceCatalog> findByTypeAndVin(
            @Param("type") MaintenanceCatalogType type,
            @Param("vin") String vin
    );

    boolean existsByName(String name);

}
