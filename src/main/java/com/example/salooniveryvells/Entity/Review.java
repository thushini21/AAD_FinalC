package com.example.salooniveryvells.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking; // Associated booking

    @Column(nullable = false)
    private int rating; // Rating (1-5)

    @Column
    private String comment; // Review comment
}