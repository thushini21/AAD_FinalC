package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    // Custom query methods can be added here
    List<Notification> findByUser_UserId(int userId); // Find notifications by user ID
}