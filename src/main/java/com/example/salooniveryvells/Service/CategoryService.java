package com.example.salooniveryvells.Service;


import com.example.salooniveryvells.Dto.CategoryDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;

public interface CategoryService {
    int addCategory(CategoryDTO categoryDTO);
    ResponseDTO getAllCategories();
    ResponseDTO getAllCategoryIds();
    ResponseDTO getCategoryIdByName(String categoryName);
    boolean hasAssociatedServices(int categoryId);
    ResponseDTO getCategoryById(int categoryId);
    int updateCategory(CategoryDTO categoryDTO);
    ResponseDTO deleteCategory(int categoryId);
}