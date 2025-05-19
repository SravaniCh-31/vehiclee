package com.vehicleregistration.service;

import com.vehicleregistration.model.Owner;
import com.vehicleregistration.model.Registration;
import com.vehicleregistration.repository.OwnerRepository;
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

public class OwnerServiceTest {

    @InjectMocks
    private OwnerService ownerService;

    @Mock
    private OwnerRepository ownerRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for addOwner - success
    @Test
    public void testAddOwner_Success() {
        Owner owner = new Owner();
        owner.setName("John Doe");

        when(ownerRepository.existsByName(owner.getName())).thenReturn(false);
        when(ownerRepository.save(owner)).thenReturn(owner);

        Owner result = ownerService.addOwner(owner);

        assertNotNull(result);
        assertEquals(owner.getName(), result.getName());
        verify(ownerRepository, times(1)).existsByName(owner.getName());
        verify(ownerRepository, times(1)).save(owner);
    }

    // Test for addOwner - duplicate name
    @Test
    public void testAddOwner_DuplicateName_ThrowsException() {
        Owner owner = new Owner();
        owner.setName("John Doe");

        when(ownerRepository.existsByName(owner.getName())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ownerService.addOwner(owner));

        assertEquals("Owner with the name 'John Doe' already exists.", exception.getMessage());
        verify(ownerRepository, times(1)).existsByName(owner.getName());
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    // Test for getAllOwners - success
    @Test
    public void testGetAllOwners_Success() {
        Owner owner = new Owner();
        owner.setName("John Doe");

        when(ownerRepository.findAll()).thenReturn(List.of(owner));

        List<Owner> result = ownerService.getAllOwners();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(owner.getName(), result.get(0).getName());
        verify(ownerRepository, times(1)).findAll();
    }

    // Test for getOwnerById - success
    @Test
    public void testGetOwnerById_Success() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("John Doe");

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

        Owner result = ownerService.getOwnerById(1L);

        assertNotNull(result);
        assertEquals(owner.getId(), result.getId());
        assertEquals(owner.getName(), result.getName());
        verify(ownerRepository, times(1)).findById(1L);
    }

    // Test for getOwnerById - owner not found
    @Test
    public void testGetOwnerById_NotFound_ThrowsException() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ownerService.getOwnerById(1L));

        assertEquals("Owner not found with ID: 1", exception.getMessage());
        verify(ownerRepository, times(1)).findById(1L);
    }

    // Test for patchOwner - success
    @Test
    public void testPatchOwner_Success() {
        Owner existingOwner = new Owner();
        existingOwner.setId(1L);
        existingOwner.setName("John Doe");

        Owner updatedOwner = new Owner();
        updatedOwner.setName("Jane Doe");
        updatedOwner.setPhone("+123456789");

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(existingOwner));
        when(ownerRepository.save(existingOwner)).thenReturn(existingOwner);

        Owner result = ownerService.patchOwner(1L, updatedOwner);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("+123456789", result.getPhone());
        verify(ownerRepository, times(1)).findById(1L);
        verify(ownerRepository, times(1)).save(existingOwner);
    }

    // Test for patchOwner - owner not found
    @Test
    public void testPatchOwner_NotFound_ThrowsException() {
        Owner updatedOwner = new Owner();
        updatedOwner.setName("Jane Doe");

        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ownerService.patchOwner(1L, updatedOwner));

        assertEquals("Owner not found with ID: 1", exception.getMessage());
        verify(ownerRepository, times(1)).findById(1L);
    }

    // Test for deleteOwner - success
    @Test
    public void testDeleteOwner_Success() {
        Owner owner = new Owner();
        owner.setId(1L);

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        doNothing().when(ownerRepository).delete(owner);

        assertDoesNotThrow(() -> ownerService.deleteOwner(1L));
        verify(ownerRepository, times(1)).findById(1L);
        verify(ownerRepository, times(1)).delete(owner);
    }

    // Test for deleteOwner - owner not found
    @Test
    public void testDeleteOwner_NotFound_ThrowsException() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ownerService.deleteOwner(1L));

        assertEquals("Owner not found with ID: 1", exception.getMessage());
        verify(ownerRepository, times(1)).findById(1L);
        verify(ownerRepository, never()).delete(any(Owner.class));
    }

    // Test for searchOwnerByName - success
    @Test
    public void testSearchOwnerByName_Success() {
        Owner owner = new Owner();
        owner.setName("John Doe");

        when(ownerRepository.findByNameContainingIgnoreCase("John")).thenReturn(List.of(owner));

        List<Owner> result = ownerService.searchOwnerByName("John");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(ownerRepository, times(1)).findByNameContainingIgnoreCase("John");
    }

    // Test for searchOwnerByName - empty result
    @Test
    public void testSearchOwnerByName_NoMatch_ThrowsException() {
        when(ownerRepository.findByNameContainingIgnoreCase("Nonexistent")).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ownerService.searchOwnerByName("Nonexistent"));

        assertEquals("No owners found matching the name: Nonexistent", exception.getMessage());
        verify(ownerRepository, times(1)).findByNameContainingIgnoreCase("Nonexistent");
    }
}
