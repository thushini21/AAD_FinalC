package com.example.salooniveryvells.Dto;

import lombok.Data;

@Data
public class ServiceDTO {
    private int serviceId; // Unique identifier for the service
    private String serviceName; // Name of the service
    private String description; // Description of the service
    private double fixedPrice; // Fixed price for the service
    private double hourlyRate; // Hourly rate for time-based pricing
    private String image; // Optional field for service image
    private int categoryId; // ID of the associated category
    private int serviceProviderId; // ID of the associated service provider
}
