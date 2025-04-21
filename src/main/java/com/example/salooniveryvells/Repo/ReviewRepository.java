package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Entity.Booking;
import com.example.salooniveryvells.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // In ReviewRepository.java
    Optional<Review> findByBooking(Booking booking);
    boolean existsByBooking(Booking booking);
}