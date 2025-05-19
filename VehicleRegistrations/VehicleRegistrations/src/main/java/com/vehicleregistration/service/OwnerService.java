package com.vehicleregistration.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vehicleregistration.model.Owner;
import com.vehicleregistration.model.Registration;
import com.vehicleregistration.model.Vehicle;
import com.vehicleregistration.repository.OwnerRepository;
import com.vehicleregistration.repository.RegistrationRepository;
import com.vehicleregistration.repository.VehicleRepository;

import jakarta.transaction.Transactional;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private RegistrationRepository registrationRepository;
    // ✅ Add new owner with duplicate name check
    @Transactional
    public Owner addOwner(Owner owner) {
        if (ownerRepository.existsByName(owner.getName())) {
            throw new RuntimeException("Owner with the name '" + owner.getName() + "' already exists.");
        }

        // Ensure all registrations in the owner have the owner set
        if (owner.getRegistrations() != null) {
            for (Registration registration : owner.getRegistrations()) {
                registration.setOwner(owner);
            }
        }
        return ownerRepository.save(owner);
    }

    // ✅ Get all owners
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    // ✅ Get owner by ID
    public Owner getOwnerById(Long id) {
        // Fetch the owner from the repository
        return ownerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + id));
    }


    // ✅ Update owner by ID
    public Owner patchOwner(Long id, Owner updatedOwner) {
        // Fetch the existing owner or throw an exception if not found
        Owner existingOwner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + id));

        // Update only non-null fields in the incoming object
        if (updatedOwner.getName() != null) {
            existingOwner.setName(updatedOwner.getName());
        }
        if (updatedOwner.getAddress() != null) {
            existingOwner.setAddress(updatedOwner.getAddress());
        }
        if (updatedOwner.getPhone() != null) {
            existingOwner.setPhone(updatedOwner.getPhone());
        }

        // Handle partial updates for registrations
        if (updatedOwner.getRegistrations() != null) {
            for (Registration updatedRegistration : updatedOwner.getRegistrations()) {
                // Find if the registration exists
                Optional<Registration> existingRegistration = existingOwner.getRegistrations().stream()
                        .filter(r -> r.getId().equals(updatedRegistration.getId()))
                        .findFirst();

                if (existingRegistration.isPresent()) {
                    Registration reg = existingRegistration.get();
                    if (updatedRegistration.getRegistrationNumber() != null) {
                        reg.setRegistrationNumber(updatedRegistration.getRegistrationNumber());
                    }
                    if (updatedRegistration.getRegistrationDate() != null) {
                        reg.setRegistrationDate(updatedRegistration.getRegistrationDate());
                    }
                    if (updatedRegistration.getVehicle() != null) {
                        reg.setVehicle(updatedRegistration.getVehicle());
                    }
                } else {
                    // Add a new registration if it doesn't exist
                    updatedRegistration.setOwner(existingOwner);
                    existingOwner.getRegistrations().add(updatedRegistration);
                }
            }
        }

        // Save the updated owner back to the repository
        return ownerRepository.save(existingOwner);
    } 
    
    @Transactional
    public Owner updateDetails(Long ownerId, String registrationNumber, Owner updatedOwner) {
        // Fetch owner
    	System.out.println(updatedOwner);
        Owner existingOwner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));

        // Update owner details
        existingOwner.setName(updatedOwner.getName());
        existingOwner.setAddress(updatedOwner.getAddress());
        existingOwner.setPhone(updatedOwner.getPhone());

        // Fetch existing registration
        Registration existingRegistration = registrationRepository.findByRegistrationNumberContainingIgnoreCase(registrationNumber);
        if (existingRegistration == null || !existingRegistration.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Registration not found for the given owner.");
        }
        // Update registration details
        for (Registration updatedRegistration : updatedOwner.getRegistrations()) {
        	System.out.println(updatedRegistration.getRegistrationNumber());
            existingRegistration.setRegistrationNumber(updatedRegistration.getRegistrationNumber());
            existingRegistration.setRegistrationDate(updatedRegistration.getRegistrationDate());
            // Update vehicle details
            Vehicle existingVehicle = existingRegistration.getVehicle();
            if (existingVehicle != null && updatedRegistration.getVehicle() != null) {
                Vehicle updatedVehicle = updatedRegistration.getVehicle();
                
                existingVehicle.setLicenseNumber(updatedVehicle.getLicenseNumber());
                existingVehicle.setVin(updatedVehicle.getVin());
                existingVehicle.setMake(updatedVehicle.getMake());
                existingVehicle.setModel(updatedVehicle.getModel());
                existingVehicle.setVehicleName(updatedVehicle.getVehicleName());
                existingVehicle.setVehicleType(updatedVehicle.getVehicleType());

                // Explicitly save vehicle
                vehicleRepository.save(existingVehicle);
            }
        }
        return ownerRepository.save(existingOwner);
    }


    // ✅ Delete owner by ID
    public void deleteOwner(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + id));
        ownerRepository.delete(owner);
    }


    // ✅ Search owners by partial name (case-insensitive)
    public List<Owner> searchOwnerByName(String name) {
        // Validate the input
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name cannot be null or empty.");
        }

        // Perform the search
        List<Owner> owners = ownerRepository.findByNameContainingIgnoreCase(name);

        // Check if the result list is empty
        if (owners.isEmpty()) {
            throw new RuntimeException("No owners found matching the name: " + name);
        }

        return owners;
    }

}
