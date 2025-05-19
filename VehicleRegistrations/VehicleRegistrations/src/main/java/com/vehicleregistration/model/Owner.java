package com.vehicleregistration.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Getter
@Setter
@ToString
@Table(name = "Owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required.")
    @Column(nullable = false, length = 100) // Ensures non-null and limits column length
    private String name;

    @NotBlank(message = "Address is required.")
    @Column(nullable = false, length = 200)
    private String address;

    @NotBlank(message = "Phone number is required.")
    @Column(nullable = false, unique = true, length = 15) // Adds uniqueness for phone number
    private String phone;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations;
}
