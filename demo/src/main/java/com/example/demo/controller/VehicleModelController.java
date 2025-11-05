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
        public ResponseEntity<EnumSchemaResponse> getVehicleModelEnum() {
            return ResponseEntity.ok(vehicleModelService.getModelEnumSchema());
        }

        @GetMapping
        public ResponseEntity<List<VehicleModelResponse>> getAllVehicleModels() {
            return ResponseEntity.ok(vehicleModelService.getAll());
        }

        @GetMapping("/{id}")
        public ResponseEntity<VehicleModelResponse> getVehicleModelById(@PathVariable Long id) {
            return ResponseEntity.ok(vehicleModelService.getById(id));
        }

        @PostMapping
        public ResponseEntity<VehicleModelResponse> createVehicleModel(@Valid @RequestBody VehicleModelRequest.CreateModel request) {
            return new ResponseEntity<>(vehicleModelService.create(request), HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        public ResponseEntity<VehicleModelResponse> updateVehicleModel(
                @PathVariable Long id,
                @Valid @RequestBody VehicleModelRequest.UpdateModel request) {
            return ResponseEntity.ok(vehicleModelService.update(id, request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteVehicleModel(@PathVariable Long id) {
            vehicleModelService.delete(id);
            return ResponseEntity.noContent().build();
        }
    }
