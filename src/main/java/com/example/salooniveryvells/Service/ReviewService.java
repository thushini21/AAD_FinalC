package com.example.salooniveryvells.Service;


import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.ReviewDTO;

public interface ReviewService {
    ResponseDTO addReview(ReviewDTO reviewDTO);
    ResponseDTO getReviewByBookingId(int bookingId);
    ResponseDTO getAllReviews();
    ResponseDTO getReviewById(int reviewId);
    ResponseDTO updateReview(int reviewId, ReviewDTO reviewDTO);
    ResponseDTO deleteReview(int reviewId);
}