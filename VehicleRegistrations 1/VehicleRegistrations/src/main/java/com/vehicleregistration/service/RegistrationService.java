package com.vehicleregistration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vehicleregistration.model.Owner;
import com.vehicleregistration.model.Registration;
import com.vehicleregistration.model.Vehicle;
import com.vehicleregistration.repository.OwnerRepository;
import com.vehicleregistration.repository.RegistrationRepository;
import com.vehicleregistration.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private OwnerRepository ownerRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    
    // Create a new registration
    public Registration createRegistrationByOwnerName(Registration registration, String ownerName) {
        // Fetch the owner by name or throw an exception if not found
        Owner owner = ownerRepository.findByName(ownerName);
        if (owner == null) {
            throw new RuntimeException("Owner not found with name: " + ownerName);
        }

        // Link the fetched owner to the registration
        registration.setOwner(owner);

        // Validate and handle the vehicle
        if (registration.getVehicle() == null) {
            throw new IllegalArgumentException("Vehicle details must be provided.");
        }

        Vehicle vehicle = registration.getVehicle();
        vehicle = vehicleRepository.save(vehicle);
        // Link the vehicle to the registration
        registration.setVehicle(vehicle);

        // Validate registration constraints
        String regNumber = registration.getRegistrationNumber();

        if (registrationRepository.existsByOwnerIdAndVehicleId(owner.getId(), vehicle.getId())) {
            throw new RuntimeException("The owner has already registered this vehicle.");
        }

        if (regNumber == null || regNumber.isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or empty.");
        }

        if (registrationRepository.existsByRegistrationNumber(regNumber)) {
            throw new RuntimeException("Registration number already exists.");
        }

        // Save the registration and return the saved object
        return registrationRepository.save(registration);
    }



    // Get a specific registration by ID
    public Optional<Registration> getRegistrationById(Long id) {
        return registrationRepository.findById(id);
    }

    // Get all registrations
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    // Delete a registration by ID
    public boolean deleteRegistration(Long id) {
        if (registrationRepository.existsById(id)) {
            registrationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Search registrations by a partial registration number
    public List<Registration> searchByRegistrationNumber(String keyword) {
        return registrationRepository.findByRegistrationNumberContainingIgnoreCase(keyword);
    }
    
    
    public Registration patchRegistration(Long id, Registration partialRegistration) {
        return registrationRepository.findById(id).map(existingRegistration -> {
            // Update non-null fields only
            if (partialRegistration.getRegistrationNumber() != null) {
                existingRegistration.setRegistrationNumber(partialRegistration.getRegistrationNumber());
            }
            if (partialRegistration.getRegistrationDate() != null) {
                existingRegistration.setRegistrationDate(partialRegistration.getRegistrationDate());
            }
            if (partialRegistration.getOwner() != null) {
                existingRegistration.setOwner(partialRegistration.getOwner());
            }
            // Save updated registration
            return registrationRepository.save(existingRegistration);
        }).orElseThrow(() -> new RuntimeException("Registration not found with ID: " + id));
    }

}
