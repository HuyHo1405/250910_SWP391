package com.example.demo.repo;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepo extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVinAndEntityStatus(String vin, EntityStatus status);

    Optional<Vehicle> findByVin(String vin);

    List<Vehicle> findByEntityStatus(EntityStatus status);

    List<Vehicle> findAllByEntityStatus(EntityStatus status);

    List<Vehicle> findByCustomerIdAndEntityStatus(Long customerId, EntityStatus status);

    boolean existsByPlateNumberAndEntityStatus(String plateNumber, EntityStatus status);

    boolean existsByVinAndEntityStatus(String vin, EntityStatus status);

    @Modifying
    @Query("UPDATE Vehicle v SET v.entityStatus = :status WHERE v.vin = :vin")
    int updateStatus(@Param("vin") String vin, @Param("status") EntityStatus status);

    @Modifying
    @Query("UPDATE Vehicle v SET v.entityStatus = 'INACTIVE' WHERE v.vin = :vin")
    int softDelete(@Param("vin") String vin);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.entityStatus = 'ACTIVE'")
    long countActive();

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.customer.id = :customerId AND v.entityStatus = 'ACTIVE'")
    long countActiveByUser(@Param("customerId") Long customerId);
}
