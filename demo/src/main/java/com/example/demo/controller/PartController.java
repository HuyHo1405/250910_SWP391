package com.example.demo.controller;

import com.example.demo.model.dto.ErrorResponse;
import com.example.demo.model.dto.PartRequest;
import com.example.demo.model.dto.PartResponse;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.service.interfaces.IPartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/api/parts")
@RequiredArgsConstructor
@Tag(name = "Part Management")
public class PartController {

    private final IPartService partService;

    @PostMapping
    @Operation(
            summary = "Create new part",
            description = "Create a new part record. Requires admin or authorized staff permission. Part number must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
            {
              "id": 33,
              "name": "Battery X",
              "partNumber": 101,
              "manufacturer": "Tesla",
              "description": "New battery",
              "currentUnitPrice": 0.1,
              "quantity": 110,
              "status": "ACTIVE",
              "createdAt": "2025-10-23T11:28:43.3724123"
            }
            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or missing required field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
            {
              "code": "INVALID_INPUT",
              "message": "partNumber: must not be blank, name: must not be blank",
              "timestamp": "2025-10-23T10:17:12.385",
              "path": "uri=/api/parts"
            }
            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not admin or authorized staff)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
            {
              "code": "FORBIDDEN",
              "message": "You are not authorized to create parts",
              "timestamp": "2025-10-23T10:18:44.092",
              "path": "uri=/api/parts"
            }
            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate part number detected",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
            {
              "code": "ALREADY_EXISTS",
              "message": "Part already exists with number: BRK-001",
              "timestamp": "2025-10-23T10:20:10.741",
              "path": "uri=/api/parts"
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
              "timestamp": "2025-10-23T10:21:55.145",
              "path": "uri=/api/parts"
            }
            """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> createPart(@Valid @RequestBody PartRequest request) {
        PartResponse response = partService.createPart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get part by ID",
            description = "Retrieve detailed information of a specific part by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
            {
              "id": 33,
              "partNumber": 101,
              "name": "Battery X",
              "manufacturer": "Tesla",
              "description": "New battery",
              "currentUnitPrice": 0.1,
              "quantity": 110,
              "status": "ACTIVE",
              "createdAt": "2025-10-23T11:28:43.3724123"
            }
            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
            {
              "code": "NOT_FOUND",
              "message": "Part not found with id: 999",
              "timestamp": "2025-10-23T11:30:15.257",
              "path": "uri=/api/parts/999"
            }
            """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long id) {
        PartResponse response = partService.getPartById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/part-number/{partNumber}")
    @Operation(
            summary = "Get part by part number",
            description = "Retrieve detailed information of a specific part by its unique part number."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "id": 33,
          "partNumber": 101,
          "name": "Battery X",
          "manufacturer": "Tesla",
          "description": "New battery",
          "currentUnitPrice": 0.1,
          "quantity": 110,
          "status": "ACTIVE",
          "createdAt": "2025-10-23T11:28:43.3724123"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "Part not found with part number: 9999",
          "timestamp": "2025-10-23T11:32:15.251",
          "path": "uri=/api/parts/part-number/9999"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> getPartByPartNumber(@PathVariable Integer partNumber) {
        PartResponse response = partService.getPartByPartNumber(partNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Get all parts",
            description = "Retrieve a list of parts with optional filters for manufacturer, status, and search keyword."
    )
    @Parameters({
            @Parameter(
                    name = "manufacturer",
                    description = "Filter parts by manufacturer name.",
                    required = false,
                    example = "Tesla"
            ),
            @Parameter(
                    name = "status",
                    description = "Filter parts by entity status (e.g., ACTIVE, INACTIVE).",
                    required = false,
                    example = "ACTIVE"
            ),
            @Parameter(
                    name = "search",
                    description = "Search parts by name or description keyword.",
                    required = false,
                    example = "battery"
            )
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Parts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PartResponse.class)),
                            examples = @ExampleObject(
                                    value = """
        [
          {
            "id": 33,
            "partNumber": 101,
            "name": "Battery X",
            "manufacturer": "Tesla",
            "description": "New battery",
            "currentUnitPrice": 0.1,
            "quantity": 110,
            "status": "ACTIVE",
            "createdAt": "2025-10-23T11:28:43.3724123"
          },
          {
            "id": 34,
            "partNumber": 102,
            "name": "Motor Y",
            "manufacturer": "Toyota",
            "description": "High performance motor",
            "currentUnitPrice": 0.25,
            "quantity": 85,
            "status": "ACTIVE",
            "createdAt": "2025-10-22T09:19:52.1231412"
          }
        ]
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No parts found matching the filters",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "No parts found with specified filters",
          "timestamp": "2025-10-23T11:34:28.251",
          "path": "uri=/api/parts"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<List<PartResponse>> getAllParts(
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) EntityStatus status,
            @RequestParam(required = false) String search) {
        List<PartResponse> parts = partService.getAllPartsFiltered(manufacturer, status, search);
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/low-stock")
    @Operation(
            summary = "Get low stock parts",
            description = "Retrieve a list of parts with quantity below a specified threshold."
    )
    @Parameter(
            name = "threshold",
            description = "The quantity threshold to identify low stock parts. Parts with quantity less than or equal to this value will be returned.",
            required = false,
            example = "10",
            schema = @Schema(type = "integer", defaultValue = "10")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Low stock parts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PartResponse.class)),
                            examples = @ExampleObject(
                                    value = """
        [
          {
            "id": 35,
            "partNumber": 103,
            "name": "Brake Pad",
            "manufacturer": "Honda",
            "description": "Front brake pad",
            "currentUnitPrice": 0.15,
            "quantity": 8,
            "status": "ACTIVE",
            "createdAt": "2025-10-20T14:22:11.5512321"
          },
          {
            "id": 36,
            "partNumber": 104,
            "name": "Oil Filter",
            "manufacturer": "Ford",
            "description": "Engine oil filter",
            "currentUnitPrice": 0.05,
            "quantity": 5,
            "status": "ACTIVE",
            "createdAt": "2025-10-19T10:15:33.2314512"
          }
        ]
        """
                            )
                    )
            ),
    })
    public ResponseEntity<List<PartResponse>> getLowStockParts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<PartResponse> parts = partService.getLowStockParts(threshold);
        return ResponseEntity.ok(parts);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update part by ID",
            description = "Update detailed information of a specific part by its unique ID. Requires admin or authorized staff permission."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Part details to update",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PartRequest.class),
                    examples = @ExampleObject(
                            value = """
        {
          "partNumber": 101,
          "name": "Battery X Pro",
          "manufacturer": "Tesla",
          "description": "Updated battery with enhanced capacity",
          "currentUnitPrice": 0.12,
          "quantity": 120,
          "status": "ACTIVE"
        }
        """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "id": 33,
          "partNumber": 101,
          "name": "Battery X Pro",
          "manufacturer": "Tesla",
          "description": "Updated battery with enhanced capacity",
          "currentUnitPrice": 0.12,
          "quantity": 120,
          "status": "ACTIVE",
          "createdAt": "2025-10-23T11:28:43.3724123"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or missing required field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "INVALID_INPUT",
          "message": "partNumber: must not be blank, name: must not be blank",
          "timestamp": "2025-10-23T12:24:12.385",
          "path": "uri=/api/parts/33"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not admin or authorized staff)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "FORBIDDEN",
          "message": "You are not authorized to update parts",
          "timestamp": "2025-10-23T12:24:44.092",
          "path": "uri=/api/parts/33"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "Part not found with id: 999",
          "timestamp": "2025-10-23T12:24:55.257",
          "path": "uri=/api/parts/999"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate part number detected",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "ALREADY_EXISTS",
          "message": "Part already exists with number: 101",
          "timestamp": "2025-10-23T12:25:10.741",
          "path": "uri=/api/parts/33"
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
          "timestamp": "2025-10-23T12:25:55.145",
          "path": "uri=/api/parts/33"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody PartRequest request) {
        PartResponse response = partService.updatePart(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/price")
    @Operation(
            summary = "Update part price",
            description = "Update the unit price of a specific part. Requires admin or authorized staff permission."
    )
    @Parameter(
            name = "newPrice",
            description = "The new unit price for the part",
            required = true,
            example = "0.15"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part price updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "id": 33,
          "partNumber": 101,
          "name": "Battery X",
          "manufacturer": "Tesla",
          "description": "New battery",
          "currentUnitPrice": 0.15,
          "quantity": 110,
          "status": "ACTIVE",
          "createdAt": "2025-10-23T11:28:43.3724123"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid price value",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "INVALID_INPUT",
          "message": "Price must be greater than zero",
          "timestamp": "2025-10-23T12:26:12.385",
          "path": "uri=/api/parts/33/price"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not admin or authorized staff)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "FORBIDDEN",
          "message": "You are not authorized to update part price",
          "timestamp": "2025-10-23T12:26:44.092",
          "path": "uri=/api/parts/33/price"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "Part not found with id: 999",
          "timestamp": "2025-10-23T12:26:55.257",
          "path": "uri=/api/parts/999/price"
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
          "timestamp": "2025-10-23T12:27:55.145",
          "path": "uri=/api/parts/33/price"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> updatePartPrice(
            @PathVariable Long id,
            @RequestParam Double newPrice) {
        PartResponse response = partService.updatePartPrice(id, newPrice);
        return ResponseEntity.ok(response);
    }

    // ================================
    // XÓA: /quantity, /adjust-stock
    // CHỈ GIỮ 2 ENDPOINTS QUAN TRỌNG:
    // ================================

    @PostMapping("/{id}/stock/increase")
    @Operation(
            summary = "Increase part stock",
            description = "Increase the stock quantity of a specific part. Requires admin or authorized staff permission."
    )
    @Parameter(
            name = "amount",
            description = "The amount to increase stock by",
            required = true,
            example = "50"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part stock increased successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "id": 33,
          "partNumber": 101,
          "name": "Battery X",
          "manufacturer": "Tesla",
          "description": "New battery",
          "currentUnitPrice": 0.1,
          "quantity": 160,
          "status": "ACTIVE",
          "createdAt": "2025-10-23T11:28:43.3724123"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid amount value",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "INVALID_INPUT",
          "message": "Amount must be greater than zero",
          "timestamp": "2025-10-23T12:26:12.385",
          "path": "uri=/api/parts/33/stock/increase"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not admin or authorized staff)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "FORBIDDEN",
          "message": "You are not authorized to update stock",
          "timestamp": "2025-10-23T12:26:44.092",
          "path": "uri=/api/parts/33/stock/increase"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "Part not found with id: 999",
          "timestamp": "2025-10-23T12:26:55.257",
          "path": "uri=/api/parts/999/stock/increase"
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
          "timestamp": "2025-10-23T12:27:55.145",
          "path": "uri=/api/parts/33/stock/increase"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> increaseStock(
            @PathVariable Long id,
            @RequestParam Integer amount) {
        PartResponse response = partService.increaseStock(id, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/stock/decrease")
    @Operation(
            summary = "Decrease part stock",
            description = "Decrease the stock quantity of a specific part. Requires admin or authorized staff permission."
    )
    @Parameter(
            name = "amount",
            description = "The amount to decrease stock by",
            required = true,
            example = "20"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part stock decreased successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "id": 33,
          "partNumber": 101,
          "name": "Battery X",
          "manufacturer": "Tesla",
          "description": "New battery",
          "currentUnitPrice": 0.1,
          "quantity": 90,
          "status": "ACTIVE",
          "createdAt": "2025-10-23T11:28:43.3724123"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid amount or insufficient stock",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "INVALID_INPUT",
          "message": "Insufficient stock. Available: 110, Requested: 150",
          "timestamp": "2025-10-23T12:26:12.385",
          "path": "uri=/api/parts/33/stock/decrease"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not admin or authorized staff)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "FORBIDDEN",
          "message": "You are not authorized to update stock",
          "timestamp": "2025-10-23T12:26:44.092",
          "path": "uri=/api/parts/33/stock/decrease"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "Part not found with id: 999",
          "timestamp": "2025-10-23T12:26:55.257",
          "path": "uri=/api/parts/999/stock/decrease"
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
          "timestamp": "2025-10-23T12:27:55.145",
          "path": "uri=/api/parts/33/stock/decrease"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> decreaseStock(
            @PathVariable Long id,
            @RequestParam Integer amount) {
        PartResponse response = partService.decreaseStock(id, amount);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate part",
            description = "Deactivate a specific part by setting its status to INACTIVE. Requires admin or authorized staff permission."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part deactivated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "id": 33,
          "partNumber": 101,
          "name": "Battery X",
          "manufacturer": "Tesla",
          "description": "New battery",
          "currentUnitPrice": 0.1,
          "quantity": 110,
          "status": "INACTIVE",
          "createdAt": "2025-10-23T11:28:43.3724123"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Part is already inactive",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "INVALID_INPUT",
          "message": "Part is already inactive",
          "timestamp": "2025-10-23T12:26:12.385",
          "path": "uri=/api/parts/33/deactivate"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not admin or authorized staff)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "FORBIDDEN",
          "message": "You are not authorized to deactivate parts",
          "timestamp": "2025-10-23T12:26:44.092",
          "path": "uri=/api/parts/33/deactivate"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "Part not found with id: 999",
          "timestamp": "2025-10-23T12:26:55.257",
          "path": "uri=/api/parts/999/deactivate"
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
          "timestamp": "2025-10-23T12:27:55.145",
          "path": "uri=/api/parts/33/deactivate"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> deactivatePart(@PathVariable Long id) {
        PartResponse response = partService.deactivatePart(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(
            summary = "Reactivate part",
            description = "Reactivate a specific part by setting its status to ACTIVE. Requires admin or authorized staff permission."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Part reactivated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "id": 33,
          "partNumber": 101,
          "name": "Battery X",
          "manufacturer": "Tesla",
          "description": "New battery",
          "currentUnitPrice": 0.1,
          "quantity": 110,
          "status": "ACTIVE",
          "createdAt": "2025-10-23T11:28:43.3724123"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Part is already active",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "INVALID_INPUT",
          "message": "Part is already active",
          "timestamp": "2025-10-23T12:26:12.385",
          "path": "uri=/api/parts/33/reactivate"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied (not admin or authorized staff)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "FORBIDDEN",
          "message": "You are not authorized to reactivate parts",
          "timestamp": "2025-10-23T12:26:44.092",
          "path": "uri=/api/parts/33/reactivate"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "Part not found with id: 999",
          "timestamp": "2025-10-23T12:26:55.257",
          "path": "uri=/api/parts/999/reactivate"
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
          "timestamp": "2025-10-23T12:27:55.145",
          "path": "uri=/api/parts/33/reactivate"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<PartResponse> reactivatePart(@PathVariable Long id) {
        PartResponse response = partService.reactivatePart(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/check-availability")
    @Operation(
            summary = "Check part availability",
            description = "Check if a specific part has sufficient stock to meet the required quantity."
    )
    @Parameter(
            name = "requiredQuantity",
            description = "The quantity required for checking availability",
            required = true,
            example = "25"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Availability check completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class),
                            examples = @ExampleObject(
                                    value = "true"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid required quantity",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "INVALID_INPUT",
          "message": "Required quantity must be greater than zero",
          "timestamp": "2025-10-23T12:26:12.385",
          "path": "uri=/api/parts/33/check-availability"
        }
        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Part not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
        {
          "code": "NOT_FOUND",
          "message": "Part not found with id: 999",
          "timestamp": "2025-10-23T12:26:55.257",
          "path": "uri=/api/parts/999/check-availability"
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
          "timestamp": "2025-10-23T12:27:55.145",
          "path": "uri=/api/parts/33/check-availability"
        }
        """
                            )
                    )
            )
    })
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam Integer requiredQuantity) {
        boolean available = partService.checkAvailability(id, requiredQuantity);
        return ResponseEntity.ok(available);
    }
}

