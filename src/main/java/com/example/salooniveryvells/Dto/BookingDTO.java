package com.example.salooniveryvells.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private int bookingId; // Unique identifier for the booking
    private int customerId; // ID of the customer
    private int serviceId; // ID of the service
    private LocalDateTime bookingDateTime; // Date and time of the booking
    private String status; // Booking status (e.g., Pending, Accepted, Completed, Cancelled)
    private double hoursWorked; // Hours worked for the service (for time-based pricing)
    private LocalDateTime createAt; //booking create date and time
}
