package com.example.salooniveryvells.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private int paymentId; // Unique identifier for the payment
    private int bookingId; // ID of the associated booking
    private double depositAmount; // Deposit amount
    private double finalAmount; // Final amount
    private String status; // Payment status (e.g., Pending, Completed, Failed, Refunded)
    private LocalDateTime paymentDate; // Date and time of payment
}