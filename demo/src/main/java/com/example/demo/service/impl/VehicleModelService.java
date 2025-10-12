package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.EnumSchemaResponse;
import com.example.demo.model.dto.VehicleModelRequest;
import com.example.demo.model.dto.VehicleModelResponse;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.entity.VehicleModel;
import com.example.demo.repo.VehicleModelRepo;
import com.example.demo.service.interfaces.IVehicleModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleModelService implements IVehicleModelService{

    //
    private final AccessControlService accessControlService;
    private final VehicleModelRepo vehicleModelRepo;

    //
    @Override
    @Transactional
    public VehicleModelResponse create(VehicleModelRequest.CreateModel request) {
        log.info("Creating vehicle model: {} {}", request.getBrandName(), request.getModelName());

        // Check permission (no ownership check - models are system resources)
        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "create");

        // Check if model already exists
        if (vehicleModelRepo.existsByBrandNameAndModelNameAndStatus(request.getBrandName(), request.getModelName(), EntityStatus.ACTIVE)) {
            throw new CommonException.AlreadyExists("Vehicle Model", "brand and model",
                    request.getBrandName() + " " + request.getModelName());
        }

        VehicleModel vehicleModel = VehicleModel.builder()
                .brandName(request.getBrandName())
                .modelName(request.getModelName())
                .dimensions(request.getDimensions())
                .yearIntroduce(request.getYearIntroduce())
                .seats(request.getSeats())
                .batteryCapacityKwh(request.getBatteryCapacityKwh())
                .rangeKm(request.getRangeKm())
                .chargingTimeHours(request.getChargingTimeHours())
                .motorPowerKw(request.getMotorPowerKw())
                .weightKg(request.getWeightKg())
                .status(request.getStatus() != null ? request.getStatus() : EntityStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        VehicleModel saved = vehicleModelRepo.save(vehicleModel);
        log.info("Vehicle model created successfully with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    //
    @Override
    @Transactional(readOnly = true)
    public List<VehicleModelResponse> getByStatus(EntityStatus status) {
        log.info("Fetching vehicle models with status: {}", status);

        // Check permission
        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "read-by-status");

        return vehicleModelRepo.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleModelResponse> getAll() {
        log.info("Fetching all vehicle models");

        // Check permission (everyone can read if they have permission)
        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "read");

        return vehicleModelRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleModelResponse getById(Long id) {
        log.info("Fetching vehicle model with ID: {}", id);

        // Check permission (everyone can read if they have permission)
        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "read");

        VehicleModel vehicleModel = vehicleModelRepo.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle Model", id));

        return mapToResponse(vehicleModel);
    }

    @Override
    @Transactional(readOnly = true)
    public EnumSchemaResponse getModelEnumSchema() {
        List<String> modelNames = vehicleModelRepo.findModelNamesByStatus(EntityStatus.ACTIVE);
        return new EnumSchemaResponse(
                "VehicleModelEnum",          // name
                modelNames,                  // enumValue
                "List of car models for sale" // description
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleModelResponse> getByBrandName(String brandName) {
        log.info("Fetching vehicle models for brand: {}", brandName);

        // Check permission
        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "read");

        return vehicleModelRepo.findByBrandNameAndStatus(brandName, EntityStatus.ACTIVE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //
    @Override
    @Transactional
    public VehicleModelResponse update(Long id, VehicleModelRequest.UpdateModel request) {
        log.info("Updating vehicle model with ID: {}", id);

        // Check permission (no ownership check)
        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "update");

        VehicleModel vehicleModel = vehicleModelRepo.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle Model", id));

        // Check if updating to a name that already exists (excluding current model)
        vehicleModelRepo.findByBrandNameAndModelNameAndStatus(request.getBrandName(), request.getModelName(), EntityStatus.ACTIVE)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new CommonException.AlreadyExists("Vehicle Model", "brand and model",
                                request.getBrandName() + " " + request.getModelName());
                    }
                });

        vehicleModel.setBrandName(request.getBrandName());
        vehicleModel.setModelName(request.getModelName());
        vehicleModel.setDimensions(request.getDimensions());
        vehicleModel.setYearIntroduce(request.getYearIntroduce());
        vehicleModel.setSeats(request.getSeats());
        vehicleModel.setBatteryCapacityKwh(request.getBatteryCapacityKwh());
        vehicleModel.setRangeKm(request.getRangeKm());
        vehicleModel.setChargingTimeHours(request.getChargingTimeHours());
        vehicleModel.setMotorPowerKw(request.getMotorPowerKw());
        vehicleModel.setWeightKg(request.getWeightKg());
        vehicleModel.setStatus(request.getStatus());

        VehicleModel updated = vehicleModelRepo.save(vehicleModel);
        log.info("Vehicle model updated successfully with ID: {}", id);
        return mapToResponse(updated);
    }

    //
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting vehicle model with ID: {}", id);

        // Check permission (only ADMIN has delete permission via database)
        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "delete");

        if (!vehicleModelRepo.existsById(id)) {
            throw new CommonException.NotFound("Vehicle Model", id);
        }

        // Optional: Check if model is in use before deletion
        // Uncomment when you want to enforce this rule
        // if (vehicleRepo.existsByModelId(id)) {
        //     throw new VehicleException.ModelInUse(id);
        // }

        vehicleModelRepo.deleteById(id);
        log.info("Vehicle model deleted successfully with ID: {}", id);
    }

    //
    private VehicleModelResponse mapToResponse(VehicleModel vehicleModel) {
        return VehicleModelResponse.builder()
                .id(vehicleModel.getId())
                .brandName(vehicleModel.getBrandName())
                .modelName(vehicleModel.getModelName())
                .dimensions(vehicleModel.getDimensions())
                .yearIntroduce(vehicleModel.getYearIntroduce())
                .seats(vehicleModel.getSeats())
                .batteryCapacityKwh(vehicleModel.getBatteryCapacityKwh())
                .rangeKm(vehicleModel.getRangeKm())
                .chargingTimeHours(vehicleModel.getChargingTimeHours())
                .motorPowerKw(vehicleModel.getMotorPowerKw())
                .weightKg(vehicleModel.getWeightKg())
                .status(vehicleModel.getStatus())
                .createdAt(vehicleModel.getCreatedAt())
                .build();
    }

}
