package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    // Custom query methods can be added here
    List<Service> findByCategory_CategoryId(int categoryId); // Find services by category ID
    List<Service> findByServiceProvider_UserId(int serviceProviderId); // Find services by service provider ID

    boolean existsByCategoryCategoryId(int categoryId);
    // Find all services by category ID
    Service findByServiceId(int serviceId);
    @Query("SELECT s FROM Service s WHERE s.category.categoryId = :categoryId")
    List<Service> findByCategoryId(@Param("categoryId") int categoryId);

}