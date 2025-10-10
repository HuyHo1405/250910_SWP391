package com.example.demo.controller;

import com.example.demo.model.dto.VehicleModelRequest;
import com.example.demo.model.dto.VehicleModelResponse;
import com.example.demo.model.entity.EntityStatus;
import com.example.demo.service.interfaces.IVehicleModelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-models")
@RequiredArgsConstructor
@Tag(name = "Vehicle Model")
public class VehicleModelController {

    private final IVehicleModelService vehicleModelService;

    @PostMapping
    public ResponseEntity<VehicleModelResponse> createVehicleModel(@Valid @RequestBody VehicleModelRequest.CreateModel request) {
        VehicleModelResponse response = vehicleModelService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleModelResponse> updateVehicleModel(
            @PathVariable Long id,
            @Valid @RequestBody VehicleModelRequest.UpdateModel request) {
        VehicleModelResponse response = vehicleModelService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleModelResponse> getVehicleModelById(@PathVariable Long id) {
        VehicleModelResponse response = vehicleModelService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleModelResponse>> getAllVehicleModels() {
        List<VehicleModelResponse> responses = vehicleModelService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VehicleModelResponse>> getVehicleModelsByStatus(@PathVariable EntityStatus status) {
        List<VehicleModelResponse> responses = vehicleModelService.getByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/brand/{brandName}")
    public ResponseEntity<List<VehicleModelResponse>> getVehicleModelsByBrand(@PathVariable String brandName) {
        List<VehicleModelResponse> responses = vehicleModelService.getByBrandName(brandName);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicleModel(@PathVariable Long id) {
        vehicleModelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
