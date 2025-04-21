package com.example.salooniveryvells.Controller;

import com.example.salooniveryvells.Dto.MessageDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<ResponseDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        ResponseDTO response = messageService.sendMessage(messageDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllMessages() {
        ResponseDTO response = messageService.getAllMessages();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getMessageById(@PathVariable int id) {
        ResponseDTO response = messageService.getMessageById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateMessage(@PathVariable int id, @RequestBody MessageDTO messageDTO) {
        ResponseDTO response = messageService.updateMessage(id, messageDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteMessage(@PathVariable int id) {
        ResponseDTO response = messageService.deleteMessage(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}