package com.vehicleregistration.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vehicleregistration.model.Vehicle;
import com.vehicleregistration.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

	@Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle patchVehicle(Long id, Vehicle partialVehicle) {
        return vehicleRepository.findById(id).map(existingVehicle -> {
            // Update non-null fields only
            if (partialVehicle.getLicenseNumber() != null) {
                existingVehicle.setLicenseNumber(partialVehicle.getLicenseNumber());
            }
            if (partialVehicle.getVin() != null) {
                existingVehicle.setVin(partialVehicle.getVin());
            }
            if (partialVehicle.getMake() != null) {
                existingVehicle.setMake(partialVehicle.getMake());
            }
            if (partialVehicle.getModel() != null) {
                existingVehicle.setModel(partialVehicle.getModel());
            }
            if (partialVehicle.getVehicleName() != null) {
                existingVehicle.setVehicleName(partialVehicle.getVehicleName());
            }
            if (partialVehicle.getVehicleType() != null) {
                existingVehicle.setVehicleType(partialVehicle.getVehicleType());
            }
            // Save the patched vehicle
            return vehicleRepository.save(existingVehicle);
        }).orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + id));
    }


    public boolean deleteVehicle(Long id) {
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Vehicle> searchVehicles(String name, String type, String licenseNumber) {
        if (name != null && !name.isEmpty()) {
            return vehicleRepository.findByVehicleNameContainingIgnoreCase(name);
        } else if (type != null && !type.isEmpty()) {
            return vehicleRepository.findByVehicleTypeContainingIgnoreCase(type);
        } else if (licenseNumber != null && !licenseNumber.isEmpty()) {
            return vehicleRepository.findByLicenseNumberContainingIgnoreCase(licenseNumber);
        } else {
            return vehicleRepository.findAll();
        }
    }
}
