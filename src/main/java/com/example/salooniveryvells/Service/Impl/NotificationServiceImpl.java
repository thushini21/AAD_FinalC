package com.example.salooniveryvells.Service.Impl;


import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Dto.NotificationDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Entity.Notification;
import com.example.salooniveryvells.Entity.User;
import com.example.salooniveryvells.Repo.NotificationRepository;
import com.example.salooniveryvells.Repo.UserRepository;
import com.example.salooniveryvells.Service.NotificationService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseDTO addNotification(NotificationDTO notificationDTO) {
        if (notificationRepository.existsById(notificationDTO.getNotificationId())) {
            return new ResponseDTO(400, "Notification already exists with id: " + notificationDTO.getNotificationId(), null);
        }

        // Fetch the user from the database
        User user = userRepository.findById(notificationDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + notificationDTO.getUserId()));

        // Map NotificationDTO to Notification entity
        Notification notification = modelMapper.map(notificationDTO, Notification.class);
        notification.setUser(user); // Set the user
        notification.setTimestamp(LocalDateTime.now()); // Set the current timestamp

        notificationRepository.save(notification);
        return new ResponseDTO(200, "Notification added successfully", notificationDTO);
    }

    @Override
    public ResponseDTO getAllNotifications() {
        List<NotificationDTO> notificationList = modelMapper.map(notificationRepository.findAll(),
                new TypeToken<List<NotificationDTO>>() {}.getType());
        return new ResponseDTO(200, "Notifications retrieved successfully", notificationList);
    }

    @Override
    public ResponseDTO getNotificationById(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        NotificationDTO notificationDTO = modelMapper.map(notification, NotificationDTO.class);
        notificationDTO.setUserId(notification.getUser().getUserId()); // Set user ID
        return new ResponseDTO(200, "Notification retrieved successfully", notificationDTO);
    }

    @Override
    public ResponseDTO updateNotification(int notificationId, NotificationDTO notificationDTO) {
        if (!notificationRepository.existsById(notificationId)) {
            return new ResponseDTO(404, "Notification not found with id: " + notificationId, null);
        }

        // Fetch the user from the database
        User user = userRepository.findById(notificationDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + notificationDTO.getUserId()));

        // Map NotificationDTO to Notification entity
        Notification notification = modelMapper.map(notificationDTO, Notification.class);
        notification.setNotificationId(notificationId); // Ensure the ID is preserved
        notification.setUser(user); // Set the user

        notificationRepository.save(notification);
        return new ResponseDTO(200, "Notification updated successfully", notificationDTO);
    }

    @Override
    public ResponseDTO deleteNotification(int notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            return new ResponseDTO(404, "Notification not found with id: " + notificationId, null);
        }
        notificationRepository.deleteById(notificationId);
        return new ResponseDTO(200, "Notification deleted successfully", null);
    }
}