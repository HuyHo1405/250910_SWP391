package com.example.demo.service.interfaces;

import com.example.demo.model.dto.VehicleModelRequest;
import com.example.demo.model.dto.VehicleModelResponse;
import com.example.demo.model.entity.EntityStatus;

import java.util.List;

public interface IVehicleModelService {
    VehicleModelResponse create(VehicleModelRequest.CreateModel request);
    VehicleModelResponse update(Long id, VehicleModelRequest.UpdateModel request);
    VehicleModelResponse getById(Long id);
    List<VehicleModelResponse> getAll();
    List<VehicleModelResponse> getByStatus(EntityStatus status);
    List<VehicleModelResponse> getByBrandName(String brandName);
    void delete(Long id);
}
