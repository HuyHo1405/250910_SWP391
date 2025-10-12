package com.example.demo.service.interfaces;

import com.example.demo.model.dto.EnumSchemaResponse;
import com.example.demo.model.dto.VehicleModelRequest;
import com.example.demo.model.dto.VehicleModelResponse;
import com.example.demo.model.modelEnum.EntityStatus;

import java.util.List;

public interface IVehicleModelService {
    //
    VehicleModelResponse create(VehicleModelRequest.CreateModel request);

    //
    List<VehicleModelResponse> getAll();
    VehicleModelResponse getById(Long id);
    List<VehicleModelResponse> getByBrandName(String brandName);
    List<VehicleModelResponse> getByStatus(EntityStatus status);
    EnumSchemaResponse getModelEnumSchema();

    //
    VehicleModelResponse update(Long id, VehicleModelRequest.UpdateModel request);

    //
    void delete(Long id);
}
