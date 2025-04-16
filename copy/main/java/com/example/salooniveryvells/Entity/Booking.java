package com.example.salooniveryvells.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer; // Many-to-one relationship with User (customer)

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service; // Many-to-one relationship with Service

    @Column(name = "booking_date_time", nullable = false)
    private LocalDateTime bookingDateTime; // Date and time of the booking

    @Column(nullable = false)
    private String status; // Booking status (e.g., Pending, Accepted, Completed, Cancelled)

    @Column
    private double hoursWorked; // Hours worked for the service (for time-based pricing)

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment; // One-to-one relationship with Payment

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Timestamp for when the booking was created
}

