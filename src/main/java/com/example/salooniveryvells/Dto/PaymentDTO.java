package com.example.salooniveryvells.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private int paymentId;

    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be positive")
    private int bookingId;


    @PositiveOrZero(message = "Deposit amount must be zero or positive")
    @Digits(integer = 8, fraction = 2, message = "Deposit amount must have up to 8 integer and 2 fraction digits")
    private double depositAmount;


    @PositiveOrZero(message = "Final amount must be zero or positive")
    @Digits(integer = 8, fraction = 2, message = "Final amount must have up to 8 integer and 2 fraction digits")
    private double finalAmount;

    @NotBlank(message = "Status is required")
    private String status;

    @PastOrPresent(message = "Payment date cannot be in the future")
    private LocalDateTime paymentDate;
}