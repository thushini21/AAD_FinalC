package com.example.salooniveryvells.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductDTO {
    private int productId;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String productName;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 integer and 2 fraction digits")
    private double price;

    @Pattern(regexp = "^(https?://.*\\.(?:png|jpg|jpeg|gif|webp))?$",
            message = "Image must be a valid URL ending with .png, .jpg, .jpeg, .gif, or .webp")
    private String image;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    private String contactNumber;

    @NotNull(message = "Service provider ID is required")
    @Positive(message = "Service provider ID must be positive")
    private int serviceProviderId;
}