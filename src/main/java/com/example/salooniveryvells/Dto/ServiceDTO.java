package com.example.salooniveryvells.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ServiceDTO {
    private int serviceId;

    @NotBlank(message = "Service name is required")
    @Size(min = 2, max = 100, message = "Service name must be between 2 and 100 characters")
    private String serviceName;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @PositiveOrZero(message = "Fixed price must be zero or positive")
    @Digits(integer = 6, fraction = 2, message = "Fixed price must have up to 6 integer and 2 fraction digits")
    private double fixedPrice;

    @PositiveOrZero(message = "Hourly rate must be zero or positive")
    @Digits(integer = 6, fraction = 2, message = "Hourly rate must have up to 6 integer and 2 fraction digits")
    private double hourlyRate;

    private String image; // Optional field, no validation needed

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private int categoryId;

    @NotNull(message = "Service provider ID is required")
    @Positive(message = "Service provider ID must be positive")
    private int serviceProviderId;
}