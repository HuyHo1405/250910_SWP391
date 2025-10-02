package com.example.demo.repo;

import com.example.demo.model.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleModelRepo extends JpaRepository<VehicleModel, Long> {
}
