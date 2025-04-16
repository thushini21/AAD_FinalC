package com.example.salooniveryvells.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serviceId;  // Changed to Long (JPA best practice)

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "fixed_price")
    private Double fixedPrice;  // Nullable, use Double instead of double

    @Column(name = "hourly_rate")
    private Double hourlyRate;  // Nullable, use Double instead of double

    @Column
    private String image;  // Stores file path or URL

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id", nullable = false)
    private User serviceProvider;

    // Helper methods for setting IDs
    public void setCategoryId(int categoryId) {
        if (categoryId != 0) {
            this.category = new Category();
            this.category.setCategoryId(categoryId);
        }
    }

    public void setServiceProviderId(int serviceProviderId) {
        if (serviceProviderId != 0) {
            this.serviceProvider = new User();
            this.serviceProvider.setUserId(serviceProviderId);
        }
    }

}
