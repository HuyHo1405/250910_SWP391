package com.example.demo.controller;

import com.example.demo.model.dto.MaintenanceServiceRequest;
import com.example.demo.model.dto.MaintenanceServiceResponse;
import com.example.demo.service.interfaces.IMaintenanceServiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/maintenance-services")
@RequiredArgsConstructor
@Tag(name = "Maintenance Service")
public class MaintenanceServiceController {

    private final IMaintenanceServiceService serviceService;

    @PostMapping
    public MaintenanceServiceResponse create(@Valid @RequestBody MaintenanceServiceRequest request) {
        return serviceService.createService(request);
    }

    @GetMapping
    public List<MaintenanceServiceResponse> list() {
        return serviceService.listServices();
    }

    @GetMapping("/{id}")
    public MaintenanceServiceResponse get(@PathVariable Long id) {
        return serviceService.getService(id);
    }

    @PutMapping("/{id}")
    public MaintenanceServiceResponse update(@PathVariable Long id, @Valid @RequestBody MaintenanceServiceRequest request) {
        return serviceService.updateService(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serviceService.deleteService(id);
    }
}
