package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    // Custom query methods can be added here
    List<Message> findByChat_ChatId(int chatId); // Find messages by chat ID
}