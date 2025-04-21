package com.example.salooniveryvells.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatId;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; // First participant in the chat

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2; // Second participant in the chat

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages; // List of messages in the chat

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Timestamp for when the chat was created
}