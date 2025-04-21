package com.example.salooniveryvells.Service.Impl;

import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Dto.ChatDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Entity.Chat;
import com.example.salooniveryvells.Entity.User;
import com.example.salooniveryvells.Repo.ChatRepository;
import com.example.salooniveryvells.Repo.UserRepository;
import com.example.salooniveryvells.Service.ChatService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseDTO createChat(ChatDTO chatDTO) {
        if (chatRepository.existsById(chatDTO.getChatId())) {
            return new ResponseDTO(400, "Chat already exists with id: " + chatDTO.getChatId(), null);
        }

        // Fetch the users from the database
        User user1 = userRepository.findById(chatDTO.getUser1Id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + chatDTO.getUser1Id()));
        User user2 = userRepository.findById(chatDTO.getUser2Id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + chatDTO.getUser2Id()));

        // Map ChatDTO to Chat entity
        Chat chat = modelMapper.map(chatDTO, Chat.class);
        chat.setUser1(user1); // Set the first user
        chat.setUser2(user2); // Set the second user

        chatRepository.save(chat);
        return new ResponseDTO(200, "Chat created successfully", chatDTO);
    }

    @Override
    public ResponseDTO getAllChats() {
        List<ChatDTO> chatList = modelMapper.map(chatRepository.findAll(),
                new TypeToken<List<ChatDTO>>() {}.getType());
        return new ResponseDTO(200, "Chats retrieved successfully", chatList);
    }

    @Override
    public ResponseDTO getChatById(int chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
        ChatDTO chatDTO = modelMapper.map(chat, ChatDTO.class);
        chatDTO.setUser1Id(chat.getUser1().getUserId()); // Set user1 ID
        chatDTO.setUser2Id(chat.getUser2().getUserId()); // Set user2 ID
        return new ResponseDTO(200, "Chat retrieved successfully", chatDTO);
    }

    @Override
    public ResponseDTO updateChat(int chatId, ChatDTO chatDTO) {
        if (!chatRepository.existsById(chatId)) {
            return new ResponseDTO(404, "Chat not found with id: " + chatId, null);
        }

        // Fetch the users from the database
        User user1 = userRepository.findById(chatDTO.getUser1Id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + chatDTO.getUser1Id()));
        User user2 = userRepository.findById(chatDTO.getUser2Id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + chatDTO.getUser2Id()));

        // Map ChatDTO to Chat entity
        Chat chat = modelMapper.map(chatDTO, Chat.class);
        chat.setChatId(chatId); // Ensure the ID is preserved
        chat.setUser1(user1); // Set the first user
        chat.setUser2(user2); // Set the second user

        chatRepository.save(chat);
        return new ResponseDTO(200, "Chat updated successfully", chatDTO);
    }

    @Override
    public ResponseDTO deleteChat(int chatId) {
        if (!chatRepository.existsById(chatId)) {
            return new ResponseDTO(404, "Chat not found with id: " + chatId, null);
        }
        chatRepository.deleteById(chatId);
        return new ResponseDTO(200, "Chat deleted successfully", null);
    }
}