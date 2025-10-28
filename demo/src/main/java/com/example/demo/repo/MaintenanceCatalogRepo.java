package com.example.demo.repo;

import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceCatalogRepo extends JpaRepository<MaintenanceCatalog, Long> {

    @Query("SELECT DISTINCT mc FROM MaintenanceCatalog mc " +
            "LEFT JOIN mc.models mcm " +
            "LEFT JOIN mcm.vehicleModel vm " +
            "LEFT JOIN Vehicle v ON v.model.id = vm.id " +
            "WHERE (:type IS NULL OR mc.maintenanceServiceType = :type) " +
            "AND (:vin IS NULL OR v.vin = :vin)")
    List<MaintenanceCatalog> findByTypeAndVin(
            @Param("type") MaintenanceCatalogType type,
            @Param("vin") String vin
    );

    @Query("SELECT CASE WHEN COUNT(mc) > 0 THEN true ELSE false END " +
            "FROM MaintenanceCatalog mc " +
            "LEFT JOIN mc.models mcm " +
            "LEFT JOIN mcm.vehicleModel vm " +
            "LEFT JOIN Vehicle v ON v.model.id = vm.id " +
            "WHERE mc.id = :catalogId " +
            "AND v.vin = :vin")
    boolean isServiceValidForVin(
            @Param("catalogId") Long catalogId,
            @Param("vin") String vin
    );


    Optional<MaintenanceCatalog> findByName(String name);

    boolean existsByName(String name);

}
