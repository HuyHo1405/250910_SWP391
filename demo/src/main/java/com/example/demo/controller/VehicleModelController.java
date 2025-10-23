    package com.example.demo.controller;

    import com.example.demo.model.dto.EnumSchemaResponse;
    import com.example.demo.model.dto.ErrorResponse;
    import com.example.demo.model.dto.VehicleModelRequest;
    import com.example.demo.model.dto.VehicleModelResponse;
    import com.example.demo.model.modelEnum.EntityStatus;
    import com.example.demo.service.interfaces.IVehicleModelService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.ExampleObject;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

        @GetMapping("/enum")
        @Operation(
                summary = "Get vehicle model schema enums",
                description = "Retrieve enumeration metadata used by frontend such as model fields and enum values."
        )
        @ApiResponse(
                responseCode = "200",
                description = "Enum schema retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = EnumSchemaResponse.class),
                        examples = @ExampleObject(
                                value = """
                            {
                              "name": "VehicleModelEnum",
                              "enumValue": [
                                "Model S",
                                "VF8"
                              ],
                              "description": "List of vehicle models",
                              "type": "string"
                            }
                            """
                        )
                )
        )
        public ResponseEntity<EnumSchemaResponse> getVehicleModelEnum() {
            return ResponseEntity.ok(vehicleModelService.getModelEnumSchema());
        }

        @GetMapping
        @Operation(
                summary = "Get all vehicle models",
                description = "Retrieve list of all active vehicle models (public access)."
        )
        @ApiResponse(
                responseCode = "200",
                description = "List all vehicle models",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = VehicleModelResponse.class),
                        examples = @ExampleObject(
                                value = """
                            [
                              {
                                "id": 1,
                                "brandName": "Tesla",
                                "modelName": "Model S",
                                "dimensions": "4970x1964x1445",
                                "yearIntroduce": "2022",
                                "seats": 5,
                                "batteryCapacityKwh": 100,
                                "rangeKm": 650,
                                "chargingTimeHours": 1.5,
                                "motorPowerKw": 500,
                                "weightKg": 2100,
                                "status": "ACTIVE",
                                "createdAt": "2025-10-22T17:10:10.67"
                              },
                              {
                                "id": 2,
                                "brandName": "VinFast",
                                "modelName": "VF8",
                                "dimensions": "4750x1900x1660",
                                "yearIntroduce": "2023",
                                "seats": 5,
                                "batteryCapacityKwh": 90,
                                "rangeKm": 550,
                                "chargingTimeHours": 2,
                                "motorPowerKw": 400,
                                "weightKg": 2200,
                                "status": "ACTIVE",
                                "createdAt": "2025-10-22T17:10:10.67"
                              },
                              {
                                "id": 3,
                                "brandName": "Toyota",
                                "modelName": "Corolla Cross EV",
                                "dimensions": "4460x1825x1620",
                                "yearIntroduce": "2023",
                                "seats": 5,
                                "batteryCapacityKwh": 60,
                                "rangeKm": 400,
                                "chargingTimeHours": 1.8,
                                "motorPowerKw": 150,
                                "weightKg": 1600,
                                "status": "INACTIVE",
                                "createdAt": "2025-10-22T17:10:10.67"
                              }
                            ]
                            """
                        )
                )
        )
        public ResponseEntity<List<VehicleModelResponse>> getAllVehicleModels() {
            return ResponseEntity.ok(vehicleModelService.getAll());
        }

        @GetMapping("/{id}")
        @Operation(
                summary = "Get vehicle model by ID",
                description = "Retrieve details of a vehicle model by its ID (public access)."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Vehicle model found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = VehicleModelResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                    {
                                        "id": 1,
                                        "brandName": "Tesla",
                                        "modelName": "Model S",
                                        "dimensions": "4970x1964x1445",
                                        "yearIntroduce": "2022",
                                        "seats": 5,
                                        "batteryCapacityKwh": 100,
                                        "rangeKm": 650,
                                        "chargingTimeHours": 1.5,
                                        "motorPowerKw": 500,
                                        "weightKg": 2100,
                                        "status": "ACTIVE",
                                        "createdAt": "2025-10-22T17:10:10.67"
                                    }
                                    """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Vehicle model not found",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = """
                                    {
                                      "code": "NOT_FOUND",
                                      "message": "Vehicle model not found: 99",
                                      "timestamp": "2025-10-22T11:30:00",
                                      "path": "uri=/api/vehicle-models/99"
                                    }
                                    """
                                )
                        )
                )
        })
        public ResponseEntity<VehicleModelResponse> getVehicleModelById(@PathVariable Long id) {
            return ResponseEntity.ok(vehicleModelService.getById(id));
        }

        @GetMapping("/brand/{brandName}")
        @Operation(
                summary = "Get vehicle models by brand",
                description = "List all vehicle models filtered by a given brand name (public access)."
        )
        @ApiResponse(
                responseCode = "200",
                description = "Vehicle models by brand found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = VehicleModelResponse.class),
                        examples = @ExampleObject(
                                value = """
                            [
                              {
                                "id": 1,
                                "brandName": "Tesla",
                                "modelName": "Model S",
                                "dimensions": "4970x1964x1445",
                                "yearIntroduce": "2022",
                                "seats": 5,
                                "batteryCapacityKwh": 100,
                                "rangeKm": 650,
                                "chargingTimeHours": 1.5,
                                "motorPowerKw": 500,
                                "weightKg": 2100,
                                "status": "ACTIVE",
                                "createdAt": "2025-10-22T17:10:10.67"
                              }
                            ]
                            """
                        )
                )
        )
        public ResponseEntity<List<VehicleModelResponse>> getVehicleModelsByBrand(@PathVariable String brandName) {
            return ResponseEntity.ok(vehicleModelService.getByBrandName(brandName));
        }

        @GetMapping("/status/{status}")
        @Operation(
                summary = "Get vehicle models by status",
                description = "Retrieve vehicle models based on their entity status (ACTIVE / INACTIVE / DELETED). Require admin permission."
        )
        @ApiResponse(
                responseCode = "200",
                description = "Vehicle models by status found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = VehicleModelResponse.class),
                        examples = @ExampleObject(
                                value = """
                            [
                              {
                                "id": 1,
                                "brandName": "Tesla",
                                "modelName": "Model S",
                                "dimensions": "4970x1964x1445",
                                "yearIntroduce": "2022",
                                "seats": 5,
                                "batteryCapacityKwh": 100,
                                "rangeKm": 650,
                                "chargingTimeHours": 1.5,
                                "motorPowerKw": 500,
                                "weightKg": 2100,
                                "status": "ACTIVE",
                                "createdAt": "2025-10-22T17:10:10.67"
                              },
                              {
                                "id": 2,
                                "brandName": "VinFast",
                                "modelName": "VF8",
                                "dimensions": "4750x1900x1660",
                                "yearIntroduce": "2023",
                                "seats": 5,
                                "batteryCapacityKwh": 90,
                                "rangeKm": 550,
                                "chargingTimeHours": 2,
                                "motorPowerKw": 400,
                                "weightKg": 2200,
                                "status": "ACTIVE",
                                "createdAt": "2025-10-22T17:10:10.67"
                              },
                              {
                                "id": 3,
                                "brandName": "Toyota",
                                "modelName": "Corolla Cross EV",
                                "dimensions": "4460x1825x1620",
                                "yearIntroduce": "2023",
                                "seats": 5,
                                "batteryCapacityKwh": 60,
                                "rangeKm": 400,
                                "chargingTimeHours": 1.8,
                                "motorPowerKw": 150,
                                "weightKg": 1600,
                                "status": "INACTIVE",
                                "createdAt": "2025-10-22T17:10:10.67"
                              }
                            ]
                            """
                        )
                )
        )
        public ResponseEntity<List<VehicleModelResponse>> getVehicleModelsByStatus(@PathVariable EntityStatus status) {
            return ResponseEntity.ok(vehicleModelService.getByStatus(status));
        }

        @PostMapping
        @Operation(
                summary = "Create new vehicle model",
                description = "Admin/system can create a new vehicle model. Brand/model combination must be unique."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Vehicle model created successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = VehicleModelResponse.class),
                                examples = @ExampleObject(
                                        value = """
                        {
                          "id": 8,
                          "brandName": "VinFast",
                          "modelName": "VF e34",
                          "dimensions": "4300x1793x1613",
                          "yearIntroduce": 2023,
                          "seats": 5,
                          "batteryCapacityKwh": 42.0,
                          "rangeKm": 285,
                          "chargingTimeHours": 8.5,
                          "motorPowerKw": 110,
                          "weightKg": 1760,
                          "status": "ACTIVE",
                          "createdAt": "2025-10-22T10:51:00"
                        }
                        """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = """
                        {
                          "code": "INVALID_INPUT",
                          "message": "modelName: Model name is required, brandName: Brand name is required",
                          "timestamp": "2025-10-22T16:56:48.158499",
                          "path": "uri=/api/vehicle-models"
                        }
                        """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "Vehicle model already exists",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = """
                        {
                          "code": "ALREADY_EXISTS",
                          "message": "Vehicle Model already exists with brand and model: VinFast VF e34",
                          "timestamp": "2025-10-22T10:53:00",
                          "path": "uri=/api/vehicle-models"
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
                          "timestamp": "2025-10-22T10:54:00",
                          "path": "uri=/api/vehicle-models"
                        }
                        """
                                )
                        )
                )
        })
        public ResponseEntity<VehicleModelResponse> createVehicleModel(@Valid @RequestBody VehicleModelRequest.CreateModel request) {
            return new ResponseEntity<>(vehicleModelService.create(request), HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        @PostMapping
        @Operation(
                summary = "Update vehicle model",
                description = "Admin/system can update an existing vehicle model. Brand/model combination must be unique."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Vehicle model updated successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = VehicleModelResponse.class),
                                examples = @ExampleObject(
                                        value = """
                        {
                          "id": 8,
                          "brandName": "VinFast",
                          "modelName": "VF e34",
                          "dimensions": "4300x1793x1613",
                          "yearIntroduce": 2023,
                          "seats": 5,
                          "batteryCapacityKwh": 42.0,
                          "rangeKm": 285,
                          "chargingTimeHours": 8.5,
                          "motorPowerKw": 110,
                          "weightKg": 1760,
                          "status": "ACTIVE",
                          "createdAt": "2025-10-22T10:51:00"
                        }
                        """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(
                                        value = """
                        {
                          "code": "INVALID_INPUT",
                          "message": "modelName: Model name is required, brandName: Brand name is required",
                          "timestamp": "2025-10-22T16:56:48.158499",
                          "path": "uri=/api/vehicle-models"
                        }
                        """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "Vehicle model already exists",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = """
                        {
                          "code": "ALREADY_EXISTS",
                          "message": "Vehicle Model already exists with brand and model: VinFast VF e34",
                          "timestamp": "2025-10-22T10:53:00",
                          "path": "uri=/api/vehicle-models"
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
                          "timestamp": "2025-10-22T10:54:00",
                          "path": "uri=/api/vehicle-models"
                        }
                        """
                                )
                        )
                )
        })
        public ResponseEntity<VehicleModelResponse> updateVehicleModel(
                @PathVariable Long id,
                @Valid @RequestBody VehicleModelRequest.UpdateModel request) {
            return ResponseEntity.ok(vehicleModelService.update(id, request));
        }

        @DeleteMapping("/{id}")
        @Operation(
                summary = "Delete (deactivate) vehicle model",
                description = "Soft-delete or deactivate a vehicle model by ID. Only admins can perform this action."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "204",
                        description = "Vehicle model deleted successfully (no content)"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Vehicle model not found",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = """
                                    {
                                      "code": "NOT_FOUND",
                                      "message": "Vehicle model not found with ID: 99",
                                      "timestamp": "2025-10-22T11:50:00",
                                      "path": "uri=/api/vehicle-models/99"
                                    }
                                    """
                                )
                        )
                )
        })
        public ResponseEntity<Void> deleteVehicleModel(@PathVariable Long id) {
            vehicleModelService.delete(id);
            return ResponseEntity.noContent().build();
        }
    }
