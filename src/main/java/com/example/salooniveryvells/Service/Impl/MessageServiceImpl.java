package com.example.salooniveryvells.Service.Impl;


import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Dto.MessageDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Entity.Chat;
import com.example.salooniveryvells.Entity.Message;
import com.example.salooniveryvells.Entity.User;
import com.example.salooniveryvells.Repo.ChatRepository;
import com.example.salooniveryvells.Repo.MessageRepository;
import com.example.salooniveryvells.Repo.UserRepository;
import com.example.salooniveryvells.Service.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseDTO sendMessage(MessageDTO messageDTO) {
        // Fetch the chat and sender from the database
        Chat chat = chatRepository.findById(messageDTO.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + messageDTO.getChatId()));
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + messageDTO.getSenderId()));

        // Map MessageDTO to Message entity
        Message message = modelMapper.map(messageDTO, Message.class);
        message.setChat(chat); // Set the chat
        message.setSender(sender); // Set the sender

        // Save the message
        Message savedMessage = messageRepository.save(message);

        // Map the saved message back to DTO
        MessageDTO savedMessageDTO = modelMapper.map(savedMessage, MessageDTO.class);
        return new ResponseDTO(200, "Message sent successfully", savedMessageDTO);
    }

    @Override
    public ResponseDTO getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        List<MessageDTO> messageDTOs = messages.stream()
                .map(message -> modelMapper.map(message, MessageDTO.class))
                .collect(Collectors.toList());
        return new ResponseDTO(200, "Messages retrieved successfully", messageDTOs);
    }

    @Override
    public ResponseDTO getMessageById(int id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
        return new ResponseDTO(200, "Message retrieved successfully", messageDTO);
    }

    @Override
    public ResponseDTO updateMessage(int id, MessageDTO messageDTO) {
        Message existingMessage = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        // Update the content of the message
        existingMessage.setContent(messageDTO.getContent());

        // Save the updated message
        Message updatedMessage = messageRepository.save(existingMessage);

        // Map the updated message back to DTO
        MessageDTO updatedMessageDTO = modelMapper.map(updatedMessage, MessageDTO.class);
        return new ResponseDTO(200, "Message updated successfully", updatedMessageDTO);
    }

    @Override
    public ResponseDTO deleteMessage(int id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        messageRepository.delete(message);
        return new ResponseDTO(200, "Message deleted successfully", null);
    }
}