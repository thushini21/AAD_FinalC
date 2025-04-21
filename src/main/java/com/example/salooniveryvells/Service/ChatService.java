package com.example.salooniveryvells.Service;


import com.example.salooniveryvells.Dto.ChatDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;

public interface ChatService {
    ResponseDTO createChat(ChatDTO chatDTO);
    ResponseDTO getAllChats();
    ResponseDTO getChatById(int chatId);
    ResponseDTO updateChat(int chatId, ChatDTO chatDTO);
    ResponseDTO deleteChat(int chatId);
}