package com.example.salooniveryvells.Controller;


import com.example.salooniveryvells.Dto.NotificationDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ResponseDTO> addNotification(@RequestBody NotificationDTO notificationDTO) {
        ResponseDTO response = notificationService.addNotification(notificationDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllNotifications() {
        ResponseDTO response = notificationService.getAllNotifications();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<ResponseDTO> getNotificationById(@PathVariable int notificationId) {
        ResponseDTO response = notificationService.getNotificationById(notificationId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<ResponseDTO> updateNotification(@PathVariable int notificationId, @RequestBody NotificationDTO notificationDTO) {
        ResponseDTO response = notificationService.updateNotification(notificationId, notificationDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ResponseDTO> deleteNotification(@PathVariable int notificationId) {
        ResponseDTO response = notificationService.deleteNotification(notificationId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}