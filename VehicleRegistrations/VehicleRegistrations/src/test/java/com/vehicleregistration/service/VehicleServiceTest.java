package com.vehicleregistration.service;

import com.vehicleregistration.model.Vehicle;
import com.vehicleregistration.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VehicleServiceTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for getAllVehicles - success
    @Test
    public void testGetAllVehicles_Success() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicenseNumber("ABC-123");
        vehicle.setVehicleName("Car1");

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Car1", result.get(0).getVehicleName());
        verify(vehicleRepository, times(1)).findAll();
    }

    // Test for getVehicleById - success
    @Test
    public void testGetVehicleById_Success() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setLicenseNumber("ABC-123");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        Optional<Vehicle> result = vehicleService.getVehicleById(1L);

        assertTrue(result.isPresent());
        assertEquals("ABC-123", result.get().getLicenseNumber());
        verify(vehicleRepository, times(1)).findById(1L);
    }

    // Test for getVehicleById - not found
    @Test
    public void testGetVehicleById_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleService.getVehicleById(1L);

        assertFalse(result.isPresent());
        verify(vehicleRepository, times(1)).findById(1L);
    }

    // Test for patchVehicle - success
    @Test
    public void testPatchVehicle_Success() {
        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setId(1L);
        existingVehicle.setLicenseNumber("ABC-123");
        existingVehicle.setVehicleName("Old Vehicle Name");

        Vehicle partialVehicle = new Vehicle();
        partialVehicle.setVehicleName("Updated Vehicle Name");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.save(existingVehicle)).thenReturn(existingVehicle);

        Vehicle result = vehicleService.patchVehicle(1L, partialVehicle);

        assertNotNull(result);
        assertEquals("Updated Vehicle Name", result.getVehicleName());
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).save(existingVehicle);
    }

    // Test for patchVehicle - vehicle not found
    @Test
    public void testPatchVehicle_NotFound_ThrowsException() {
        Vehicle partialVehicle = new Vehicle();
        partialVehicle.setVehicleName("Updated Vehicle Name");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> vehicleService.patchVehicle(1L, partialVehicle));

        assertEquals("Vehicle not found with ID: 1", exception.getMessage());
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    // Test for deleteVehicle - success
    @Test
    public void testDeleteVehicle_Success() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(1L);

        boolean result = vehicleService.deleteVehicle(1L);

        assertTrue(result);
        verify(vehicleRepository, times(1)).existsById(1L);
        verify(vehicleRepository, times(1)).deleteById(1L);
    }

    // Test for deleteVehicle - vehicle not found
    @Test
    public void testDeleteVehicle_NotFound() {
        when(vehicleRepository.existsById(1L)).thenReturn(false);

        boolean result = vehicleService.deleteVehicle(1L);

        assertFalse(result);
        verify(vehicleRepository, times(1)).existsById(1L);
        verify(vehicleRepository, never()).deleteById(anyLong());
    }

    // Test for searchVehicles - by name
    @Test
    public void testSearchVehicles_ByName_Success() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleName("Car1");

        when(vehicleRepository.findByVehicleNameContainingIgnoreCase("Car")).thenReturn(List.of(vehicle));

        List<Vehicle> result = vehicleService.searchVehicles("Car", null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Car1", result.get(0).getVehicleName());
        verify(vehicleRepository, times(1)).findByVehicleNameContainingIgnoreCase("Car");
    }

    // Test for searchVehicles - by type
    @Test
    public void testSearchVehicles_ByType_Success() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType("Sedan");

        when(vehicleRepository.findByVehicleTypeContainingIgnoreCase("Sedan")).thenReturn(List.of(vehicle));

        List<Vehicle> result = vehicleService.searchVehicles(null, "Sedan", null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Sedan", result.get(0).getVehicleType());
        verify(vehicleRepository, times(1)).findByVehicleTypeContainingIgnoreCase("Sedan");
    }

    // Test for searchVehicles - by license number
    @Test
    public void testSearchVehicles_ByLicenseNumber_Success() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicenseNumber("ABC-123");

        when(vehicleRepository.findByLicenseNumberContainingIgnoreCase("ABC")).thenReturn(List.of(vehicle));

        List<Vehicle> result = vehicleService.searchVehicles(null, null, "ABC");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("ABC-123", result.get(0).getLicenseNumber());
        verify(vehicleRepository, times(1)).findByLicenseNumberContainingIgnoreCase("ABC");
    }

    // Test for searchVehicles - no filters
    @Test
    public void testSearchVehicles_NoFilters_ReturnsAllVehicles() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicenseNumber("ABC-123");

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        List<Vehicle> result = vehicleService.searchVehicles(null, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("ABC-123", result.get(0).getLicenseNumber());
        verify(vehicleRepository, times(1)).findAll();
    }

    // Test for searchVehicles - no results
    @Test
    public void testSearchVehicles_NoResults_ReturnEmptyList() {
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

        List<Vehicle> result = vehicleService.searchVehicles(null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).findAll();
    }
}
