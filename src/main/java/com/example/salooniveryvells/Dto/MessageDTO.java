package com.example.salooniveryvells.Dto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private int messageId; // Unique identifier for the message
    private int chatId; // ID of the associated chat
    private int senderId; // ID of the sender
    private String content; // Content of the message
    private LocalDateTime sentAt; // Timestamp for when the message was sent
}