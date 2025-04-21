package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    // Custom query methods can be added here
    List<Chat> findByUser1_UserIdOrUser2_UserId(int user1Id, int user2Id); // Find chats involving a specific user
}