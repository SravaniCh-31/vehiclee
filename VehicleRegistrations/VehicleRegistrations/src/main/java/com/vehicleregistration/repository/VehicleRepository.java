package com.vehicleregistration.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicleregistration.model.Vehicle;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByVin(String vin);

    // Custom search methods
    List<Vehicle> findByVehicleNameContainingIgnoreCase(String name);
    List<Vehicle> findByVehicleTypeContainingIgnoreCase(String type);
    List<Vehicle> findByLicenseNumberContainingIgnoreCase(String licenseNumber);
}
