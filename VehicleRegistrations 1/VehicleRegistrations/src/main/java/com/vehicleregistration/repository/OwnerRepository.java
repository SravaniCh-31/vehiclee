package com.vehicleregistration.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vehicleregistration.model.Owner;

import java.util.List;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {  // Change Integer to Long
    boolean existsByName(String name);
    List<Owner> findByNameContainingIgnoreCase(String name);
    Owner findByName(String name);
}
