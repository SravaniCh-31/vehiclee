package com.vehicleregistration.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@ToString
@Table(name = "Registration")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private Owner owner;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotBlank(message = "Registration number is required.")
    @Column(nullable = false, unique = true, length = 50) // Enforce uniqueness
    private String registrationNumber;

    @Column(nullable = false) // Ensure the date is not null
    private LocalDateTime registrationDate;
}
