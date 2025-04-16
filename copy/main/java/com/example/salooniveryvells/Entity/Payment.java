package com.example.salooniveryvells.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking; // Many-to-one relationship with Booking

    @Column(nullable = false)
    private double depositAmount; // Deposit amount

    @Column
    private double finalAmount; // Final amount

    @Column(nullable = false)
    private String status; // Payment status (e.g., Pending, Completed, Failed, Refunded)

    @CreationTimestamp
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate; // Date and time of payment
}

