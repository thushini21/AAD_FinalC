package com.example.salooniveryvells.Service.Impl;

import com.example.salooniveryvells.Dto.CategoryDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Service.CategoryService;
import com.example.salooniveryvells.Util.VarList;
import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Entity.Category;
import com.example.salooniveryvells.Repo.CategoryRepository;
import com.example.salooniveryvells.Repo.ServiceRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private final ServiceRepository serviceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${app.base-url}") // Configure in application.properties
    private String baseUrl;

    @Value("${app.upload-dir}")
    private String uploadDir;


    public CategoryServiceImpl(ServiceRepository serviceRepository,
                               CategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean hasAssociatedServices(int categoryId) {
        return serviceRepository.existsByCategoryCategoryId(categoryId);
    }

    @Override
    @Transactional
    public int addCategory(CategoryDTO categoryDTO) {
        try {
            if (!(categoryRepository.findByCategoryName(categoryDTO.getCategoryName()) == null)) {
                return VarList.Not_Acceptable;
            }else {

                // Ensure required fields are present
                if (categoryDTO.getCategoryName() == null || categoryDTO.getCategoryName().trim().isEmpty()) {
                    return VarList.Bad_Request; // 400 - Missing name
                }

                // Manual mapping to ensure all fields are set
                Category category = new Category();
                category.setCategoryName(categoryDTO.getCategoryName());
                category.setImage(categoryDTO.getImage());
                // Set all other necessary fields...

                // Debug logging
                System.out.println("Saving category: " + category);

                Category savedCategory = categoryRepository.save(category);

                // Verify save operation
                if (savedCategory.getCategoryId() != 0) {
                    System.out.println("Saved successfully with ID: " + savedCategory.getCategoryId());
                    return VarList.Created; // 201 - Success
                } else {
                    System.out.println("Save operation failed");
                    return VarList.Internal_Server_Error; // 500 - Save failed
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in addCategory: " + e.getMessage());
            e.printStackTrace();
            return VarList.Internal_Server_Error; // 500
        }
    }


    @Override
    public ResponseDTO getCategoryIdByName(String categoryName) {
        try {
            int idByCategoryName = categoryRepository.findIdByCategoryName(categoryName);
            if (idByCategoryName == 0) {
                return new ResponseDTO(VarList.Bad_Request, "No categories found", null);
            }
            return new ResponseDTO(VarList.OK, "Success", idByCategoryName);
        }catch (Exception e) {
            return new ResponseDTO(VarList.Internal_Server_Error, "Internal server error", null);
        }
    }

    @Override
    public ResponseDTO getAllCategoryIds() {
        try {
            List<String> categoryIds = categoryRepository.findAllCategoryIds();

            if (categoryIds.isEmpty()) {
                return new ResponseDTO(VarList.Bad_Request, "No categories found", null);
            }

            return new ResponseDTO(VarList.OK, "Success", categoryIds);

        } catch (Exception e) {
            return new ResponseDTO(VarList.Internal_Server_Error, "Internal server error", null);
        }
    }

    // In your CategoryService.java
    @Override
    public ResponseDTO getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> dtos = new ArrayList<>();

        for (Category category : categories) {
            CategoryDTO dto = new CategoryDTO();
            dto.setCategoryId(category.getCategoryId());
            dto.setCategoryName(category.getCategoryName());

            // Fix image URL construction
            if (category.getImage() != null) {
                dto.setImage(category.getImage());
            } else {
                dto.setImage("");
            }

            dtos.add(dto);
        }

        return new ResponseDTO(200, "Success", dtos);
    }

    @Override
    public ResponseDTO getCategoryById(int categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        return new ResponseDTO(200, "Category retrieved successfully", categoryDTO);
    }

    @Override
    public int updateCategory(CategoryDTO categoryDTO) {
        try {
            // Find existing category
            Category existingCategory = categoryRepository.findById(categoryDTO.getCategoryId())
                    .orElse(null);

            if (existingCategory == null) {
                return VarList.Not_Found;
            }

            // Update fields
            existingCategory.setCategoryName(categoryDTO.getCategoryName());

            // Update image path if provided
            if (categoryDTO.getImage() != null && !categoryDTO.getImage().isEmpty()) {
                existingCategory.setImage(categoryDTO.getImage());
            }

            categoryRepository.save(existingCategory);
            return VarList.Updated;

        } catch (Exception e) {
            System.err.println("Category update error: " + e.getMessage());
            return VarList.Internal_Server_Error;
        }
    }
    @Override
    public ResponseDTO deleteCategory(int categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            return new ResponseDTO(404, "Category not found with id: " + categoryId, null);
        }
        categoryRepository.deleteById(categoryId);
        return new ResponseDTO(200, "Category deleted successfully", null);
    }
}
