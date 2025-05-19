package com.vehicleregistration.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@Entity
@ToString
@Table(name = "Vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License number is required.")
    @Column(nullable = false, unique = true, length = 20)
    private String licenseNumber;

    @NotBlank(message = "VIN is required.")
    private String vin;

    @NotBlank(message = "Make is required.")
    @Column(nullable = false, length = 50)
    private String make;

    @NotBlank(message = "Model is required.")
    @Column(nullable = false, length = 50)
    private String model;

    @Column(length = 100) // Optional, so no @NotBlank
    private String vehicleName;

    @Column(length = 50) // Optional, so no @NotBlank
    private String vehicleType;
}
