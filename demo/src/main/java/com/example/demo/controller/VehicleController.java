package com.example.demo.controller;

import com.example.demo.model.dto.ErrorResponse;
import com.example.demo.model.dto.VehicleRequest;
import com.example.demo.model.dto.VehicleResponse;
import com.example.demo.service.interfaces.IVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest.Create request) {
        return ResponseEntity.ok(vehicleService.createVehicle(request));
    }

    @PutMapping("/{vin}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable String vin,
            @Valid @RequestBody VehicleRequest.Update request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vin, request));
    }

    @DeleteMapping("/{vin}")
    public ResponseEntity<VehicleResponse> deleteVehicle(@PathVariable String vin) {
        return ResponseEntity.ok(vehicleService.deleteVehicle(vin));
    }

    @GetMapping("/{vin}")
    public ResponseEntity<VehicleResponse> getVehicleByVin(@PathVariable String vin) {
        return ResponseEntity.ok(vehicleService.getVehicleByVin(vin));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByUser(userId));
    }
}
