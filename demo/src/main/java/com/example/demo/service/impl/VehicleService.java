package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.VehicleRequest;
import com.example.demo.model.dto.VehicleResponse;
import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Vehicle;
import com.example.demo.model.entity.VehicleModel;
import com.example.demo.repo.UserRepo;
import com.example.demo.repo.VehicleModelRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IVehicleService;
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
public class VehicleService implements IVehicleService {

    private final AccessControlService accessControlService;
    private final VehicleRepo vehicleRepo;
    private final UserRepo userRepo;
    private final VehicleModelRepo vehicleModelRepo;

    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleRequest.Create request) {
        log.info("Creating vehicle with VIN: {}", request.getVin());

        // 1. Check access permission (ownership-based for customers)
        accessControlService.verifyResourceAccess(request.getUserId(), "VEHICLE", "create");

        // 2. Validate business rules
        validateVehicleUniqueness(request.getVin(), request.getPlateNumber());

        // 3. Fetch and validate related entities
        VehicleModel vehicleModel = getVehicleModelOrThrow(request.getVehicleModelId());
        User user = getUserOrThrow(request.getUserId());

        // 4. Create and save vehicle
        Vehicle vehicle = buildVehicle(request, user, vehicleModel);
        Vehicle saved = vehicleRepo.save(vehicle);

        log.info("Vehicle created successfully with VIN: {}", saved.getVin());
        return VehicleResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(String vin, VehicleRequest.Update request) {
        log.info("Updating vehicle with VIN: {}", vin);

        // 1. Find vehicle
        Vehicle vehicle = getActiveVehicleOrThrow(vin);

        // 2. Check access permission (ownership-based for customers)
        accessControlService.verifyResourceAccess(vehicle.getUser().getId(), "VEHICLE", "update");

        // 3. Update fields
        updateVehicleFields(vehicle, request);

        // 4. Save and return
        Vehicle updated = vehicleRepo.save(vehicle);
        log.info("Vehicle updated successfully with VIN: {}", vin);
        return VehicleResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByVin(String vin) {
        log.info("Fetching vehicle with VIN: {}", vin);

        Vehicle vehicle = getActiveVehicleOrThrow(vin);

        // Check access permission (ownership-based for customers)
        accessControlService.verifyResourceAccess(vehicle.getUser().getId(), "VEHICLE", "read");

        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByUser(Long userId) {
        log.info("Fetching vehicles for user ID: {}", userId);

        // Check access permission (ownership-based for customers)
        accessControlService.verifyResourceAccess(userId, "VEHICLE", "read");

        return vehicleRepo.findByUserIdAndEntityStatus(userId, EntityStatus.ACTIVE)
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        log.info("Fetching all vehicles");

        accessControlService.verifyCanAccessAllResources("VEHICLE", "read");

        return vehicleRepo.findAllByEntityStatus(EntityStatus.ACTIVE)
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public VehicleResponse deleteVehicle(String vin) {
        log.info("Deleting vehicle with VIN: {}", vin);

        Vehicle vehicle = getActiveVehicleOrThrow(vin);

        // Check access permission (ownership-based for customers)
        accessControlService.verifyResourceAccess(vehicle.getUser().getId(), "VEHICLE", "delete");

        vehicleRepo.softDelete(vin);
        vehicle.setEntityStatus(EntityStatus.INACTIVE);

        log.info("Vehicle soft-deleted successfully with VIN: {}", vin);
        return VehicleResponse.fromEntity(vehicle);
    }

    // === Private Helper Methods ===

    private void validateVehicleUniqueness(String vin, String plateNumber) {
        if (vehicleRepo.existsByVinAndEntityStatus(vin, EntityStatus.ACTIVE)) {
            throw new CommonException.AlreadyExists("Vehicle", "VIN", vin);
        }
        if (vehicleRepo.existsByPlateNumberAndEntityStatus(plateNumber, EntityStatus.ACTIVE)) {
            throw new CommonException.AlreadyExists("Vehicle", "plate number", plateNumber);
        }
    }

    private Vehicle getActiveVehicleOrThrow(String vin) {
        return vehicleRepo.findByVinAndEntityStatus(vin, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", vin));
    }

    private User getUserOrThrow(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new CommonException.NotFound("User", userId));
    }

    private VehicleModel getVehicleModelOrThrow(Long modelId) {
        return vehicleModelRepo.findById(modelId)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle Model", modelId));
    }

    private Vehicle buildVehicle(VehicleRequest.Create request, User user, VehicleModel model) {
        return Vehicle.builder()
                .vin(request.getVin())
                .name(request.getName())
                .plateNumber(request.getPlateNumber())
                .year(request.getYear())
                .color(request.getColor())
                .distanceTraveledKm(request.getDistanceTraveledKm())
                .purchasedAt(request.getPurchasedAt())
                .createdAt(LocalDateTime.now())
                .user(user)
                .model(model)
                .entityStatus(EntityStatus.ACTIVE)
                .build();
    }

    private void updateVehicleFields(Vehicle vehicle, VehicleRequest.Update request) {
        if (request.getName() != null) {
            vehicle.setName(request.getName());
        }

        if (request.getPlateNumber() != null && !request.getPlateNumber().equals(vehicle.getPlateNumber())) {
            validatePlateNumberUniqueness(request.getPlateNumber());
            vehicle.setPlateNumber(request.getPlateNumber());
        }

        if (request.getYear() != null) {
            vehicle.setYear(request.getYear());
        }

        if (request.getColor() != null) {
            vehicle.setColor(request.getColor());
        }

        if (request.getDistanceTraveledKm() != null) {
            vehicle.setDistanceTraveledKm(request.getDistanceTraveledKm());
        }

        if (request.getPurchasedAt() != null) {
            vehicle.setPurchasedAt(request.getPurchasedAt());
        }

        if (request.getVehicleModelId() != null) {
            VehicleModel newModel = getVehicleModelOrThrow(request.getVehicleModelId());
            vehicle.setModel(newModel);
        }
    }

    private void validatePlateNumberUniqueness(String plateNumber) {
        if (vehicleRepo.existsByPlateNumberAndEntityStatus(plateNumber, EntityStatus.ACTIVE)) {
            throw new CommonException.AlreadyExists("Vehicle", "plate number", plateNumber);
        }
    }
}
