package com.vehicleregistration.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vehicleregistration.model.Vehicle;
import com.vehicleregistration.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

//    @PostMapping
//    public ResponseEntity<?> createVehicle(@Valid @RequestBody Vehicle vehicle) {
//        try {
//            Vehicle created = vehicleService.createVehicle(vehicle);
//            return ResponseEntity.status(201).body(created);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(409).body(e.getMessage());
//        }
//    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        Vehicle updatedVehicle = vehicleService.patchVehicle(id, vehicle);
        return updatedVehicle != null ? ResponseEntity.ok(updatedVehicle) : ResponseEntity.notFound().build();
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
//        if (vehicleService.deleteVehicle(id)) {
//            return ResponseEntity.ok("Vehicle with ID " + id + " deleted.");
//        }
//        return ResponseEntity.status(404).body("Vehicle not found.");
//    }

    @GetMapping("/search")
    public List<Vehicle> searchVehicles(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String type,
                                        @RequestParam(required = false) String licenseNumber) {
        return vehicleService.searchVehicles(name, type, licenseNumber);
    }

}
