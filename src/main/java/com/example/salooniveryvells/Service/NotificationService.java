package com.example.salooniveryvells.Service;


import com.example.salooniveryvells.Dto.NotificationDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;

public interface NotificationService {
    ResponseDTO addNotification(NotificationDTO notificationDTO);
    ResponseDTO getAllNotifications();
    ResponseDTO getNotificationById(int notificationId);
    ResponseDTO updateNotification(int notificationId, NotificationDTO notificationDTO);
    ResponseDTO deleteNotification(int notificationId);
}