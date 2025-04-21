package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Custom query methods can be added here
        Category findByCategoryName(String categoryName); // Find category by name
        @Query("SELECT c.categoryName FROM Category c")
        List<String> findAllCategoryIds();

        @Query("SELECT c.categoryId FROM Category c WHERE c.categoryName = :categoryName")
        Integer findIdByCategoryName(@Param("categoryName") String categoryName);

}