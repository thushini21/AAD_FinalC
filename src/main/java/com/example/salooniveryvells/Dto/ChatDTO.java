package com.example.salooniveryvells.Dto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatDTO {
    private int chatId; // Unique identifier for the chat
    private int user1Id; // ID of the first participant in the chat
    private int user2Id; // ID of the second participant in the chat
    private List<MessageDTO> messages; // List of messages in the chat
    private LocalDateTime createdAt; // Timestamp for when the chat was created
}