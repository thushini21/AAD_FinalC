package com.example.salooniveryvells.Service;


import com.example.salooniveryvells.Dto.MessageDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;

public interface MessageService {
    ResponseDTO sendMessage(MessageDTO messageDTO); // Send a new message
    ResponseDTO getAllMessages(); // Retrieve all messages
    ResponseDTO getMessageById(int id); // Retrieve a message by ID
    ResponseDTO updateMessage(int id, MessageDTO messageDTO); // Update a message
    ResponseDTO deleteMessage(int id); // Delete a message
}