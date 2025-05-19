package com.vehicleregistration.service;

import com.vehicleregistration.model.Owner;
import com.vehicleregistration.model.Registration;
import com.vehicleregistration.model.Vehicle;
import com.vehicleregistration.repository.OwnerRepository;
import com.vehicleregistration.repository.RegistrationRepository;
import com.vehicleregistration.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for createRegistrationByOwnerName - success
    @Test
    public void testCreateRegistrationByOwnerName_Success() {
        String ownerName = "John Doe";
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName(ownerName);

        Vehicle vehicle = new Vehicle();
        vehicle.setLicenseNumber("ABC-123");
        vehicle.setVin("VIN-001");

        Registration registration = new Registration();
        registration.setRegistrationNumber("REG-001");
        registration.setVehicle(vehicle);

        when(ownerRepository.findByName(ownerName)).thenReturn(owner);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(registrationRepository.existsByOwnerIdAndVehicleId(owner.getId(), vehicle.getId())).thenReturn(false);
        when(registrationRepository.existsByRegistrationNumber(registration.getRegistrationNumber())).thenReturn(false);
        when(registrationRepository.save(registration)).thenReturn(registration);

        Registration result = registrationService.createRegistrationByOwnerName(registration, ownerName);

        assertNotNull(result);
        assertEquals(ownerName, result.getOwner().getName());
        assertEquals(vehicle.getVin(), result.getVehicle().getVin());
        assertEquals("REG-001", result.getRegistrationNumber());
        verify(ownerRepository, times(1)).findByName(ownerName);
        verify(vehicleRepository, times(1)).save(vehicle);
        verify(registrationRepository, times(1)).save(registration);
    }

    // Test for createRegistrationByOwnerName - owner not found
    @Test
    public void testCreateRegistrationByOwnerName_OwnerNotFound_ThrowsException() {
        String ownerName = "John Doe";

        Registration registration = new Registration();
        registration.setRegistrationNumber("REG-001");

        when(ownerRepository.findByName(ownerName)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> registrationService.createRegistrationByOwnerName(registration, ownerName));

        assertEquals("Owner not found with name: John Doe", exception.getMessage());
        verify(ownerRepository, times(1)).findByName(ownerName);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    // Test for getRegistrationById - success
    @Test
    public void testGetRegistrationById_Success() {
        Registration registration = new Registration();
        registration.setId(1L);
        registration.setRegistrationNumber("REG-001");

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        Optional<Registration> result = registrationService.getRegistrationById(1L);

        assertTrue(result.isPresent());
        assertEquals("REG-001", result.get().getRegistrationNumber());
        verify(registrationRepository, times(1)).findById(1L);
    }

    // Test for getRegistrationById - registration not found
    @Test
    public void testGetRegistrationById_NotFound() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Registration> result = registrationService.getRegistrationById(1L);

        assertFalse(result.isPresent());
        verify(registrationRepository, times(1)).findById(1L);
    }

    // Test for getAllRegistrations - success
    @Test
    public void testGetAllRegistrations_Success() {
        Registration registration = new Registration();
        registration.setRegistrationNumber("REG-001");

        when(registrationRepository.findAll()).thenReturn(List.of(registration));

        List<Registration> result = registrationService.getAllRegistrations();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("REG-001", result.get(0).getRegistrationNumber());
        verify(registrationRepository, times(1)).findAll();
    }

    // Test for deleteRegistration - success
    @Test
    public void testDeleteRegistration_Success() {
        when(registrationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(registrationRepository).deleteById(1L);

        boolean result = registrationService.deleteRegistration(1L);

        assertTrue(result);
        verify(registrationRepository, times(1)).existsById(1L);
        verify(registrationRepository, times(1)).deleteById(1L);
    }

    // Test for deleteRegistration - registration not found
    @Test
    public void testDeleteRegistration_NotFound() {
        when(registrationRepository.existsById(1L)).thenReturn(false);

        boolean result = registrationService.deleteRegistration(1L);

        assertFalse(result);
        verify(registrationRepository, times(1)).existsById(1L);
        verify(registrationRepository, never()).deleteById(anyLong());
    }

    // Test for searchByRegistrationNumber - success
    @Test
    public void testSearchByRegistrationNumber_Success() {
        Registration registration = new Registration();
        registration.setRegistrationNumber("REG-001");

        when(registrationRepository.findByRegistrationNumberContainingIgnoreCase("REG")).thenReturn(List.of(registration));

        List<Registration> result = registrationService.searchByRegistrationNumber("REG");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("REG-001", result.get(0).getRegistrationNumber());
        verify(registrationRepository, times(1)).findByRegistrationNumberContainingIgnoreCase("REG");
    }

    // Test for patchRegistration - success
    @Test
    public void testPatchRegistration_Success() {
        Registration existingRegistration = new Registration();
        existingRegistration.setId(1L);
        existingRegistration.setRegistrationNumber("REG-001");

        Registration partialRegistration = new Registration();
        partialRegistration.setRegistrationNumber("UPDATED-REG-001");

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(existingRegistration));
        when(registrationRepository.save(existingRegistration)).thenReturn(existingRegistration);

        Registration result = registrationService.patchRegistration(1L, partialRegistration);

        assertNotNull(result);
        assertEquals("UPDATED-REG-001", result.getRegistrationNumber());
        verify(registrationRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).save(existingRegistration);
    }

    // Test for patchRegistration - registration not found
    @Test
    public void testPatchRegistration_NotFound_ThrowsException() {
        Registration partialRegistration = new Registration();
        partialRegistration.setRegistrationNumber("UPDATED-REG-001");

        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> registrationService.patchRegistration(1L, partialRegistration));

        assertEquals("Registration not found with ID: 1", exception.getMessage());
        verify(registrationRepository, times(1)).findById(1L);
        verify(registrationRepository, never()).save(any(Registration.class));
    }
}
