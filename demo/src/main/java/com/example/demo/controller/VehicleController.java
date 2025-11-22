package com.example.demo.controller;

import com.example.demo.model.dto.VehicleRequest;
import com.example.demo.model.dto.VehicleResponse;
import com.example.demo.service.interfaces.IVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicle")
public class VehicleController {

    private final IVehicleService vehicleService;

    @PostMapping
    @Operation(summary = "[PRIVATE] [CUSTOMER] Create vehicle", description = "Allows a logged-in customer to register a new vehicle.")
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest.Create request) {
        return ResponseEntity.ok(vehicleService.createVehicle(request));
    }

    @PutMapping("/{vin}")
    @Operation(summary = "[PRIVATE] [OWNER/STAFF] Update vehicle", description = "Allows a logged-in customer to update their vehicle information.")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable String vin,
            @Valid @RequestBody VehicleRequest.Update request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vin, request));
    }

    @DeleteMapping("/{vin}")
    @Operation(summary = "[PRIVATE] [OWNER/STAFF] Delete vehicle", description = "Allows a logged-in customer to delete their vehicle.")
    public ResponseEntity<VehicleResponse> deleteVehicle(@PathVariable String vin) {
        return ResponseEntity.ok(vehicleService.deleteVehicle(vin));
    }

    @GetMapping("/{vin}")
    @Operation(summary = "[PRIVATE] [CUSTOMER] Get vehicle by VIN", description = "Returns vehicle details by VIN for the logged-in customer.")
    public ResponseEntity<VehicleResponse> getVehicleByVin(@PathVariable String vin) {
        return ResponseEntity.ok(vehicleService.getVehicleByVin(vin));
    }

    @GetMapping
    @Operation(summary = "[PRIVATE] [CUSTOMER] Get all vehicles", description = "Returns all vehicles for the logged-in customer.")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "[PRIVATE] [OWNER/STAFF] Get vehicles by user", description = "Returns all vehicles for a specific user. Requires owner or staff authentication.")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByUser(userId));
    }
}
