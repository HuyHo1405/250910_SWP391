package com.example.demo.controller;

import com.example.demo.model.dto.MaintenanceCatalogRequest;
import com.example.demo.model.dto.MaintenanceCatalogResponse;
import com.example.demo.service.interfaces.IMaintenanceCatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance-catalog")
@RequiredArgsConstructor
@Tag(name = "Service")
public class MaintenanceCatalogController {

    private final IMaintenanceCatalogService serviceService;

    @PostMapping
    public MaintenanceCatalogResponse create(@Valid @RequestBody MaintenanceCatalogRequest request) {
        return serviceService.createService(request);
    }

    @GetMapping
    public List<MaintenanceCatalogResponse> list() {
        return serviceService.listServices();
    }

    @GetMapping("/{id}")
    public MaintenanceCatalogResponse get(@PathVariable Long id) {
        return serviceService.getService(id);
    }

    @PutMapping("/{id}")
    public MaintenanceCatalogResponse update(@PathVariable Long id, @Valid @RequestBody MaintenanceCatalogRequest request) {
        return serviceService.updateService(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serviceService.deleteService(id);
    }
}
