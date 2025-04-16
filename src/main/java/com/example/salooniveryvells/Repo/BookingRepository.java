package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Find bookings by customer ID
    List<Booking> findByCustomer_UserId(int customerId);

    // Find bookings by service ID
    List<Booking> findByService_ServiceId(int serviceId);
    boolean existsByServiceServiceId(int serviceId);

    // Find bookings by status
    List<Booking> findByStatus(String status);

    // Find bookings for a service provider
    @Query("SELECT b FROM Booking b WHERE b.service.serviceProvider.userId = :providerId")
    List<Booking> findByServiceProviderId(@Param("providerId") int providerId);

    List<Booking> findByStatusIn(List<String> pending);


    // For Customers: Only fetch bookings where they are the customer
    @Query("SELECT b FROM Booking b WHERE " +
            "b.customer.userId = :customerId AND " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:fromDate IS NULL OR CAST(b.bookingDateTime AS date) >= :fromDate) AND " +
            "(:toDate IS NULL OR CAST(b.bookingDateTime AS date) <= :toDate) " +
            "ORDER BY b.bookingDateTime DESC")
    List<Booking> findByCustomerIdAndFilters(
            @Param("customerId") int customerId,
            @Param("status") String status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    // For Providers: Only fetch bookings where they are the service provider
    @Query("SELECT b FROM Booking b WHERE " +
            "b.service.serviceProvider.userId = :providerId AND " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:fromDate IS NULL OR CAST(b.bookingDateTime AS date) >= :fromDate) AND " +
            "(:toDate IS NULL OR CAST(b.bookingDateTime AS date) <= :toDate) " +
            "ORDER BY b.bookingDateTime DESC")
    List<Booking> findByProviderIdAndFilters(
            @Param("providerId") int providerId,
            @Param("status") String status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

}
