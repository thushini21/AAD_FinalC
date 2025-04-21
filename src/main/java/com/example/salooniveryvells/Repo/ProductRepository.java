package com.example.salooniveryvells.Repo;


import com.example.salooniveryvells.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Custom query methods can be added here
    List<Product> findByServiceProvider_UserId(int serviceProviderId); // Find products by service provider ID
    List<Product> findByProductNameContainingIgnoreCase(String productName); // Find products by name (case-insensitive)
}