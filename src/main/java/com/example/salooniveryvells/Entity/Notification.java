package com.example.salooniveryvells.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Associated user

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}