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
    @Operation(
            summary = "Create new vehicle",
            description = "Create vehicle for a user. Requires ownership-based (customer) or admin permission. VIN and plate number must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vehicle created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "vin": "1HGCM82633A123455",
                  "name": "string",
                  "plateNumber": "51H-12346",
                  "year": "2000",
                  "color": "string",
                  "distanceTraveledKm": 0.1,
                  "purchasedAt": "2025-10-22T09:26:28.089",
                  "createdAt": "2025-10-22T16:28:22.0468174",
                  "entityStatus": "ACTIVE",
                  "userId": 5,
                  "username": "Nguyen Van E",
                  "modelId": 1,
                  "modelName": "Model S"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or business rule violation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INVALID_INPUT",
                  "message": "vin: VIN must not be blank, plateNumber: Plate number must not be blank",
                  "timestamp": "2025-10-22T16:29:50.4050255",
                  "path": "uri=/api/vehicles"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not owner or not allowed)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "FORBIDDEN",
                  "message": "You are not authorized to access this resource",
                  "timestamp": "2025-10-22T16:31:43.9973308",
                  "path": "uri=/api/vehicles"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Related entity (user or vehicle model) not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "VehicleModel not found with id: 7",
                  "timestamp": "2025-10-22T14:33:59",
                  "path": "uri=/api/vehicles"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate VIN or plate number",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "ALREADY_EXISTS",
                  "message": "Vehicle already exists with plate number: 51H-12345",
                  "timestamp": "2025-10-22T16:34:20.3210423",
                  "path": "uri=/api/vehicles"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INTERNAL_ERROR",
                  "message": "An unexpected error occurred",
                  "timestamp": "2025-10-22T14:35:30",
                  "path": "uri=/api/vehicles"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest.Create request) {
        return ResponseEntity.ok(vehicleService.createVehicle(request));
    }

    @PutMapping("/{vin}")
    @Operation(
            summary = "Update vehicle information",
            description = "Update a vehicle identified by VIN. Only owners or admins may update. VIN cannot be changed."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vehicle updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "vin": "1HGCM82633A123455",
                  "name": "Updated Name",
                  "plateNumber": "51H-67890",
                  "year": "2021",
                  "color": "Red",
                  "distanceTraveledKm": 12345.6,
                  "purchasedAt": "2024-03-01T10:00:00",
                  "createdAt": "2025-10-22T10:00:00",
                  "entityStatus": "ACTIVE",
                  "userId": 5,
                  "username": "Nguyen Van E",
                  "modelId": 2,
                  "modelName": "Model X"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or duplicate plate number",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INVALID_INPUT",
                  "message": "plateNumber: already exists",
                  "timestamp": "2025-10-22T15:00:00",
                  "path": "uri=/api/vehicles/{vin}"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "FORBIDDEN",
                  "message": "You are not authorized to modify this vehicle",
                  "timestamp": "2025-10-22T15:02:00",
                  "path": "uri=/api/vehicles/{vin}"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vehicle not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "Vehicle not found with VIN: 1HGCM82633A123455",
                  "timestamp": "2025-10-22T15:04:00",
                  "path": "uri=/api/vehicles/{vin}"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable String vin,
            @Valid @RequestBody VehicleRequest.Update request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vin, request));
    }

    @DeleteMapping("/{vin}")
    @Operation(
            summary = "Delete (soft-delete) a vehicle",
            description = "Soft-delete a vehicle by VIN. Only owners or admins may delete."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vehicle deleted successfully (status set to INACTIVE)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "vin": "1HGCM82633A123455",
                  "entityStatus": "INACTIVE",
                  "message": "Vehicle soft-deleted successfully"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "FORBIDDEN",
                  "message": "You are not authorized to delete this vehicle",
                  "timestamp": "2025-10-22T15:08:00",
                  "path": "uri=/api/vehicles/{vin}"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vehicle not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "Vehicle not found: VIN 1HGCM82633A123455",
                  "timestamp": "2025-10-22T15:09:00",
                  "path": "uri=/api/vehicles/{vin}"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<VehicleResponse> deleteVehicle(@PathVariable String vin) {
        return ResponseEntity.ok(vehicleService.deleteVehicle(vin));
    }

    @GetMapping("/{vin}")
    @Operation(
            summary = "Get vehicle by VIN",
            description = "Fetch vehicle details by its VIN. Permission required: owner or admin."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vehicle retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class),
                            examples = @ExampleObject(
                                    name = "Success - Vehicle found",
                                    value = """
                {
                  "vin": "1HGCM82633A123455",
                  "name": "Tesla Model X",
                  "plateNumber": "51H-12345",
                  "year": "2022",
                  "color": "Black",
                  "distanceTraveledKm": 15400.5,
                  "purchasedAt": "2023-06-15T10:20:00",
                  "createdAt": "2025-10-22T09:50:12.456",
                  "entityStatus": "ACTIVE",
                  "userId": 5,
                  "username": "Nguyen Van E",
                  "modelId": 2,
                  "modelName": "Model X"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not owner or access not allowed)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "FORBIDDEN",
                  "message": "You are not authorized to view this vehicle",
                  "timestamp": "2025-10-22T10:22:00",
                  "path": "uri=/api/vehicles/1HGCM82633A123455"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vehicle not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "Vehicle not found with VIN: 1HGCM82633A123499",
                  "timestamp": "2025-10-22T10:24:00",
                  "path": "uri=/api/vehicles/1HGCM82633A123499"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<VehicleResponse> getVehicleByVin(@PathVariable String vin) {
        return ResponseEntity.ok(vehicleService.getVehicleByVin(vin));
    }

    @Operation(
            summary = "Get all vehicles",
            description = "Retrieve all active vehicles. Each entry includes model and owner information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vehicles fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class),
                            examples = @ExampleObject(
                                    name = "Success - List of vehicles by user",
                                    value = """
                [
                  {
                    "vin": "1HGCM82633A123455",
                    "name": "Tesla Model X",
                    "plateNumber": "51H-12345",
                    "year": "2022",
                    "color": "Black",
                    "distanceTraveledKm": 12000.5,
                    "purchasedAt": "2023-05-01T08:30:00",
                    "createdAt": "2025-10-22T09:55:12.046",
                    "entityStatus": "ACTIVE",
                    "userId": 5,
                    "username": "Nguyen Van E",
                    "modelId": 2,
                    "modelName": "Model X"
                  },
                  {
                    "vin": "2T1BR32E74C123456",
                    "name": "Toyota Corolla",
                    "plateNumber": "60A-56789",
                    "year": "2021",
                    "color": "White",
                    "distanceTraveledKm": 8950.2,
                    "purchasedAt": "2022-08-10T14:10:00",
                    "createdAt": "2025-10-22T09:58:45.932",
                    "entityStatus": "ACTIVE",
                    "userId": 5,
                    "username": "Nguyen Van E",
                    "modelId": 4,
                    "modelName": "Corolla"
                  }
                ]
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (access to another user's resources)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "FORBIDDEN",
                  "message": "You are not authorized to access vehicles of userId: 5",
                  "timestamp": "2025-10-22T10:28:00",
                  "path": "uri=/api/vehicles/user/5"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or has no vehicles",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "User not found: 99 or no vehicles exist",
                  "timestamp": "2025-10-22T10:30:00",
                  "path": "uri=/api/vehicles/user/99"
                }
                """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @Operation(
            summary = "Get all vehicles of a specific user",
            description = "Retrieve all active vehicles belonging to the given user ID. Each entry includes model and owner information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vehicles fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class),
                            examples = @ExampleObject(
                                    name = "Success - List of vehicles by user",
                                    value = """
                [
                  {
                    "vin": "1HGCM82633A123455",
                    "name": "Tesla Model X",
                    "plateNumber": "51H-12345",
                    "year": "2022",
                    "color": "Black",
                    "distanceTraveledKm": 12000.5,
                    "purchasedAt": "2023-05-01T08:30:00",
                    "createdAt": "2025-10-22T09:55:12.046",
                    "entityStatus": "ACTIVE",
                    "userId": 5,
                    "username": "Nguyen Van E",
                    "modelId": 2,
                    "modelName": "Model X"
                  },
                  {
                    "vin": "2T1BR32E74C123456",
                    "name": "Toyota Corolla",
                    "plateNumber": "60A-56789",
                    "year": "2021",
                    "color": "White",
                    "distanceTraveledKm": 8950.2,
                    "purchasedAt": "2022-08-10T14:10:00",
                    "createdAt": "2025-10-22T09:58:45.932",
                    "entityStatus": "ACTIVE",
                    "userId": 5,
                    "username": "Nguyen Van E",
                    "modelId": 4,
                    "modelName": "Corolla"
                  }
                ]
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (access to another user's resources)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "FORBIDDEN",
                  "message": "You are not authorized to access vehicles of userId: 5",
                  "timestamp": "2025-10-22T10:28:00",
                  "path": "uri=/api/vehicles/user/5"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or has no vehicles",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "User not found: 99 or no vehicles exist",
                  "timestamp": "2025-10-22T10:30:00",
                  "path": "uri=/api/vehicles/user/99"
                }
                """
                            )
                    )
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByUser(userId));
    }
}
