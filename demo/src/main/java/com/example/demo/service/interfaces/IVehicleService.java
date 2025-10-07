package com.example.demo.service.interfaces;

import com.example.demo.model.dto.VehicleRequest;
import com.example.demo.model.dto.VehicleResponse;

import java.util.List;

public interface IVehicleService {
    public VehicleResponse createVehicle(VehicleRequest.Create request);

    public VehicleResponse updateVehicle(String vin, VehicleRequest.Update request);

    public VehicleResponse deleteVehicle(String vin);

    public VehicleResponse getVehicleByVin(String vin);

    public List<VehicleResponse> getAllVehicles();

    public List<VehicleResponse> getVehiclesByUser(Long userId);
}
