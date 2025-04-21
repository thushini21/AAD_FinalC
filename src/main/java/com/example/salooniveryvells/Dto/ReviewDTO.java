package com.example.salooniveryvells.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewDTO {
    private int reviewId;

    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be positive")
    private int bookingId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;
}