package com.example.salooniveryvells.Dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private int bookingId;

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private int customerId;

    @NotNull(message = "Service ID is required")
    @Positive(message = "Service ID must be positive")
    private int serviceId;

    @NotNull(message = "Booking date/time is required")
    @FutureOrPresent(message = "Booking date/time must be in the present or future")
    private LocalDateTime bookingDateTime;

    private String status;

    private double hoursWorked;

    private LocalDateTime createAt;
}