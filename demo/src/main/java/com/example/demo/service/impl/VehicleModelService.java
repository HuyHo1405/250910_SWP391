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
            throw new CommonException.AlreadyExists("Mẫu xe", "hãng hay mẫu mã",
                    request.getBrandName() + " " + request.getModelName());
        }

        VehicleModel vehicleModel = VehicleModel.builder()
                .brandName(request.getBrandName())
                .modelName(request.getModelName())
                .dimensions(request.getDimensions())
                .seats(request.getSeats())
                .batteryCapacityKwh(request.getBatteryCapacityKwh())
                .rangeKm(request.getRangeKm())
                .chargingTimeHours(request.getChargingTimeHours())
                .motorPowerKw(request.getMotorPowerKw())
                .weightKg(request.getWeightKg())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
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
        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "read_by_status");

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
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe", id));

        return mapToResponse(vehicleModel);
    }

    @Override
    @Transactional(readOnly = true)
    public EnumSchemaResponse getModelEnumSchema() {
        List<String> modelNames = vehicleModelRepo.findModelNamesByStatus(EntityStatus.ACTIVE);
        return new EnumSchemaResponse(
                "VehicleModelEnum",          // name
                modelNames,                  // enumValue
                "Danh sách mẫu xe" // description
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
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe", id));

        // Check if updating to a name that already exists (excluding current model)
        vehicleModelRepo.findByBrandNameAndModelNameAndStatus(request.getBrandName(), request.getModelName(), EntityStatus.ACTIVE)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new CommonException.AlreadyExists("Mẫu xe", "hãng hay mẫu mã",
                                request.getBrandName() + " " + request.getModelName());
                    }
                });

        if (request.getBrandName() != null && !request.getBrandName().equals(vehicleModel.getBrandName())) {
            vehicleModel.setBrandName(request.getBrandName());
        }

        if (request.getModelName() != null && !request.getModelName().equals(vehicleModel.getModelName())) {
            vehicleModel.setModelName(request.getModelName());
        }

        if (request.getDimensions() != null && !request.getDimensions().equals(vehicleModel.getDimensions())) {
            vehicleModel.setDimensions(request.getDimensions());
        }

        if (request.getSeats() != null && !request.getSeats().equals(vehicleModel.getSeats())) {
            vehicleModel.setSeats(request.getSeats());
        }

        if (request.getBatteryCapacityKwh() != null && !request.getBatteryCapacityKwh().equals(vehicleModel.getBatteryCapacityKwh())) {
            vehicleModel.setBatteryCapacityKwh(request.getBatteryCapacityKwh());
        }

        if (request.getRangeKm() != null && !request.getRangeKm().equals(vehicleModel.getRangeKm())) {
            vehicleModel.setRangeKm(request.getRangeKm());
        }

        if (request.getChargingTimeHours() != null && !request.getChargingTimeHours().equals(vehicleModel.getChargingTimeHours())) {
            vehicleModel.setChargingTimeHours(request.getChargingTimeHours());
        }

        if (request.getMotorPowerKw() != null && !request.getMotorPowerKw().equals(vehicleModel.getMotorPowerKw())) {
            vehicleModel.setMotorPowerKw(request.getMotorPowerKw());
        }

        if (request.getWeightKg() != null && !request.getWeightKg().equals(vehicleModel.getWeightKg())) {
            vehicleModel.setWeightKg(request.getWeightKg());
        }

        if (request.getStatus() != null && !request.getStatus().equals(vehicleModel.getStatus())) {
            vehicleModel.setStatus(request.getStatus());
        }

        if (request.getImageUrl() != null && !request.getImageUrl().equals(vehicleModel.getImageUrl())) {
            vehicleModel.setImageUrl(request.getImageUrl());
        }

        VehicleModel updated = vehicleModelRepo.save(vehicleModel);
        log.info("Vehicle model updated successfully with ID: {}", id);
        return mapToResponse(updated);
    }

    //
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting vehicle model with ID: {}", id);

        accessControlService.verifyResourceAccessWithoutOwnership("VEHICLE_MODEL", "delete");

        VehicleModel model = vehicleModelRepo.findById(id)
            .orElseThrow(() -> new CommonException.NotFound("Mẫu xe", id));

        model.setStatus(EntityStatus.INACTIVE); // Soft delete
        vehicleModelRepo.save(model);

        log.info("Vehicle model soft-deleted (status=INACTIVE) with ID: {}", id);
    }

    //
    private VehicleModelResponse mapToResponse(VehicleModel vehicleModel) {
        return VehicleModelResponse.builder()
                .id(vehicleModel.getId())
                .brandName(vehicleModel.getBrandName())
                .modelName(vehicleModel.getModelName())
                .dimensions(vehicleModel.getDimensions())
                .seats(vehicleModel.getSeats())
                .batteryCapacityKwh(vehicleModel.getBatteryCapacityKwh())
                .rangeKm(vehicleModel.getRangeKm())
                .chargingTimeHours(vehicleModel.getChargingTimeHours())
                .motorPowerKw(vehicleModel.getMotorPowerKw())
                .weightKg(vehicleModel.getWeightKg())
                .imageUrl(vehicleModel.getImageUrl())
                .status(vehicleModel.getStatus())
                .createdAt(vehicleModel.getCreatedAt())
                .build();
    }

}
