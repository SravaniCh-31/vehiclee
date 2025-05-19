package com.vehicleregistration.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vehicleregistration.model.Registration;
import com.vehicleregistration.service.RegistrationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/registrations")
@CrossOrigin("http://127.0.0.1:5500")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // Create a new registration
    @PostMapping("/addRegsitration/{name}")
    public ResponseEntity<?> createRegistration(@Valid @RequestBody Registration registration , @PathVariable String name ) {
        try {
            Registration created = registrationService.createRegistrationByOwnerName(registration,name);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Get all registrations
    @GetMapping("/getAllRegistrations")
    public ResponseEntity<List<Registration>> getAllRegistrations() {
        try {
            List<Registration> registrations = registrationService.getAllRegistrations();
            return ResponseEntity.ok(registrations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a registration by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRegistrationById(@PathVariable Long id) {
        try {
            Optional<Registration> registration = registrationService.getRegistrationById(id);
            if (registration.isPresent()) {
                return ResponseEntity.ok(registration.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    // Update a registration by ID
    @PatchMapping("/patch/{id}")
    public ResponseEntity<?> updateRegistration(@PathVariable Long id, @RequestBody Registration registration) {
        try {
            Registration updated = registrationService.patchRegistration(id, registration);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Delete a registration by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRegistration(@PathVariable Long id) {
        try {
            if (registrationService.deleteRegistration(id)) {
                return ResponseEntity.ok("Registration with ID " + id + " deleted.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Search registrations by registration number
    @GetMapping("/search")
    public ResponseEntity<Registration> searchByRegistrationNumber(@RequestParam String query) {
        try {
            Registration results = registrationService.searchByRegistrationNumber(query);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/searchOwner/{registerNumber}")
    public ResponseEntity<?> searchByRegistrationNumberOwner(@PathVariable String registerNumber) {
        try {
            String results = registrationService.searchByRegistrationNumberOwner(registerNumber);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PutMapping("/updateOwner/{registrationNumber}/{newName}")
    public ResponseEntity<?> updateOwnerName(@PathVariable String registrationNumber, @PathVariable String newName) {
        try {
            Registration updatedRegistration = registrationService.updateOwnerName(registrationNumber, newName);
            return ResponseEntity.ok(updatedRegistration);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
