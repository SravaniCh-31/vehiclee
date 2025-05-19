package com.vehicleregistration.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vehicleregistration.model.Registration;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByOwnerIdAndVehicleId(Long ownerId, Long vehicleId);
    boolean existsByRegistrationNumber(String registrationNumber);
    Registration findByRegistrationNumberContainingIgnoreCase(String keyword);
    List<Registration> findByOwnerId(long id);
}
