package com.example.salooniveryvells.Entity;

import com.example.salooniveryvells.Enum.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // Role of the user (CUSTOMER, MANAGER, ADMIN)

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    // Additional fields for Service Providers
    @Column
    private String serviceArea; // Optional: Service area for service providers

    // Additional fields for Admins
    @Column
    private String adminLevel; // Optional: Admin level (e.g., Super Admin, Support Admin)

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus = "Pending";

    @Column(name = "status", nullable = false)
    private String status = "Active";

    @Column(name = "id_proof_path", nullable = true) // Optional for all users
    private String idProofPath;

    @Column(name = "address_proof_path", nullable = true) // Optional for all users
    private String addressProofPath;

    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Service> services;  // Services offered by this provider
}