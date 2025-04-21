package com.example.salooniveryvells.Service.Impl;


import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.ReviewDTO;
import com.example.salooniveryvells.Entity.Booking;
import com.example.salooniveryvells.Entity.Review;
import com.example.salooniveryvells.Repo.BookingRepository;
import com.example.salooniveryvells.Repo.ReviewRepository;
import com.example.salooniveryvells.Service.ReviewService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseDTO addReview(ReviewDTO reviewDTO) {
        // Check if review already exists for this booking
        if (reviewRepository.existsById(reviewDTO.getReviewId())) {
            return new ResponseDTO(400, "Review already exists with id: " + reviewDTO.getReviewId(), null);
        }

        // Fetch the booking from the database
        Booking booking = bookingRepository.findById(reviewDTO.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + reviewDTO.getBookingId()));

        // Validate booking status
        if (!"COMPLETED".equals(booking.getStatus())) {
            return new ResponseDTO(400, "Reviews can only be added for completed bookings", null);
        }

        // Validate rating
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            return new ResponseDTO(400, "Rating must be between 1 and 5", null);
        }

        // Check if review already exists for this booking
        if (reviewRepository.existsByBooking(booking)) {
            return new ResponseDTO(400, "A review already exists for this booking", null);
        }

        // Map and save review
        Review review = modelMapper.map(reviewDTO, Review.class);
        review.setBooking(booking);
        Review savedReview = reviewRepository.save(review);

        return new ResponseDTO(200, "Review added successfully", reviewDTO);
    }

    @Override
    public ResponseDTO getReviewByBookingId(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Review review = reviewRepository.findByBooking(booking)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found for booking id: " + bookingId));

        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        return new ResponseDTO(200, "Success", reviewDTO);
    }


    @Override
    public ResponseDTO getAllReviews() {
        List<ReviewDTO> reviewList = modelMapper.map(reviewRepository.findAll(),
                new TypeToken<List<ReviewDTO>>() {}.getType());
        return new ResponseDTO(200, "Reviews retrieved successfully", reviewList);
    }

    @Override
    public ResponseDTO getReviewById(int reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        reviewDTO.setBookingId(review.getBooking().getBookingId()); // Set booking ID
        return new ResponseDTO(200, "Review retrieved successfully", reviewDTO);
    }

    @Override
    public ResponseDTO updateReview(int reviewId, ReviewDTO reviewDTO) {
        if (!reviewRepository.existsById(reviewId)) {
            return new ResponseDTO(404, "Review not found with id: " + reviewId, null);
        }

        // Fetch the booking from the database
        Booking booking = bookingRepository.findById(reviewDTO.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + reviewDTO.getBookingId()));

        // Map ReviewDTO to Review entity
        Review review = modelMapper.map(reviewDTO, Review.class);
        review.setReviewId(reviewId); // Ensure the ID is preserved
        review.setBooking(booking); // Set the booking

        reviewRepository.save(review);
        return new ResponseDTO(200, "Review updated successfully", reviewDTO);
    }

    @Override
    public ResponseDTO deleteReview(int reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            return new ResponseDTO(404, "Review not found with id: " + reviewId, null);
        }
        reviewRepository.deleteById(reviewId);
        return new ResponseDTO(200, "Review deleted successfully", null);
    }
}