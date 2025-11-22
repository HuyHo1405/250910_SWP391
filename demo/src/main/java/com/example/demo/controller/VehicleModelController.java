package com.example.demo.controller;

import com.example.demo.model.dto.EnumSchemaResponse;
import com.example.demo.model.dto.VehicleModelRequest;
import com.example.demo.model.dto.VehicleModelResponse;
import com.example.demo.service.interfaces.IVehicleModelService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Vehicle Model", description = "Endpoints for managing vehicle models - Staff")
public class VehicleModelController {

    private final IVehicleModelService vehicleModelService;

    @GetMapping("/enum")
    @Operation(summary = "Get vehicle model enum schema", description = "Returns enum schema used for vehicle model fields.")
    public ResponseEntity<EnumSchemaResponse> getVehicleModelEnum() {
        return ResponseEntity.ok(vehicleModelService.getModelEnumSchema());
    }

    @GetMapping
    @Operation(summary = "Get all vehicle models", description = "Returns list of all vehicle models.")
    public ResponseEntity<List<VehicleModelResponse>> getAllVehicleModels() {
        return ResponseEntity.ok(vehicleModelService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle model by id", description = "Returns a single vehicle model by its id.")
    public ResponseEntity<VehicleModelResponse> getVehicleModelById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleModelService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a vehicle model", description = "Creates a new vehicle model; requires staff permissions.")
    public ResponseEntity<VehicleModelResponse> createVehicleModel(@Valid @RequestBody VehicleModelRequest.CreateModel request) {
        return new ResponseEntity<>(vehicleModelService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vehicle model", description = "Updates an existing vehicle model by id; requires staff permissions.")
    public ResponseEntity<VehicleModelResponse> updateVehicleModel(
            @PathVariable Long id,
            @Valid @RequestBody VehicleModelRequest.UpdateModel request) {
        return ResponseEntity.ok(vehicleModelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle model", description = "Deletes a vehicle model by id; requires staff permissions.")
    public ResponseEntity<Void> deleteVehicleModel(@PathVariable Long id) {
        vehicleModelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
