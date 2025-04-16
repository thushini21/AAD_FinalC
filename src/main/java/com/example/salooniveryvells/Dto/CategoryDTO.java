package com.example.salooniveryvells.Dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private int categoryId; // Unique identifier for the category
    private String categoryName; // Name of the category
    private String image; // Optional field for category image
}
