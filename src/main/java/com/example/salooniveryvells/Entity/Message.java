package com.example.salooniveryvells.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat; // Many-to-one relationship with Chat

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // Many-to-one relationship with User (sender)

    @Column(nullable = false)
    private String content; // Content of the message

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt; // Timestamp for when the message was sent
}