package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // Custom query methods can be added here
    Payment findByBooking_BookingId(int bookingId); // Find payment by booking ID
}