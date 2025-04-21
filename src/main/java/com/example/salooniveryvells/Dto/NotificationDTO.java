package com.example.salooniveryvells.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private int notificationId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private int userId;

    @NotBlank(message = "Message is required")
    @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
    private String message;

    @NotNull(message = "Timestamp is required")
    @PastOrPresent(message = "Timestamp cannot be in the future")
    private LocalDateTime timestamp;
}