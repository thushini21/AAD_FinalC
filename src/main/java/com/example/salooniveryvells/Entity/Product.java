package com.example.salooniveryvells.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @Column(nullable = false)
    private String productName; // Name of the product

    @Column(nullable = false)
    private String description; // Description of the product

    @Column(nullable = false)
    private double price; // Price of the product

    @Column
    private String image; // Optional field for product image

    @Column(nullable = false)
    private String contactNumber; // Contact number for inquiries

    @ManyToOne
    @JoinColumn(name = "service_provider_id", nullable = false)
    private User serviceProvider; // Many-to-one relationship with User (service provider)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Timestamp for when the product was created
}