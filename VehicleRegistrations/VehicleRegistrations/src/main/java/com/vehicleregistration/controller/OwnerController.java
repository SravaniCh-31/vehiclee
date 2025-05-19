package com.vehicleregistration.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vehicleregistration.model.Owner;
import com.vehicleregistration.service.OwnerService;

import java.util.List;

@RestController
@RequestMapping("/owners")
@CrossOrigin("http://127.0.0.1:5500")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    // Create a new owner
    @PostMapping("/save")
    public ResponseEntity<?> createOwner(@RequestBody @Valid Owner owner) {
        try {
            Owner createdOwner = ownerService.addOwner(owner);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOwner);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get all owners
    @GetMapping("/getAllOwners")
    public ResponseEntity<List<Owner>> getAllOwners() {
        try {
            List<Owner> owners = ownerService.getAllOwners();
            return ResponseEntity.ok(owners);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get owner by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOwnerById(@PathVariable Long id) {
        try {
            Owner owner = ownerService.getOwnerById(id);
            return ResponseEntity.ok(owner);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Update owner by ID
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateOwnerpatch(@PathVariable Long id, @RequestBody Owner owner) {
        try {
            Owner updatedOwner = ownerService.patchOwner(id, owner);
            return ResponseEntity.ok(updatedOwner);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Delete owner by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOwner(@PathVariable Long id) {
        try {
            ownerService.deleteOwner(id);
            return ResponseEntity.ok("Owner with ID " + id + " has been successfully deleted.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Search owners by name (case-insensitive)
    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchOwnersByName(@PathVariable String name) {
        try {
            List<Owner> owners = ownerService.searchOwnerByName(name);
            return ResponseEntity.ok(owners);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
    
    @PutMapping("/put/{id}/{registrationNumber}")
    public Owner updateOwnerPut(@PathVariable Long id, @PathVariable String registrationNumber, @RequestBody Owner owner) {
        return ownerService.updateDetails(id, registrationNumber, owner);
    }

}
