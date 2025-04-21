package com.example.salooniveryvells.Controller;


import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.ReviewDTO;
import com.example.salooniveryvells.Service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseDTO> addReview(@Valid  @RequestBody ReviewDTO reviewDTO) {
        ResponseDTO responseDTO = reviewService.addReview(reviewDTO);
        return ResponseEntity.ok()
                .body(new ResponseDTO(200, "Success", responseDTO));
    }

    @GetMapping("/get/{bookingId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ResponseDTO> getReviewByBooking(@Valid @PathVariable int bookingId) {
        ResponseDTO responseDTO = reviewService.getReviewByBookingId(bookingId);
        return ResponseEntity.ok()
                .body(new ResponseDTO(200, "Success", responseDTO));
    }
    @GetMapping
    public ResponseEntity<ResponseDTO> getAllReviews() {
        ResponseDTO response = reviewService.getAllReviews();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO> getReviewById(@Valid @PathVariable int reviewId) {
        ResponseDTO response = reviewService.getReviewById(reviewId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO> updateReview(@Valid @PathVariable int reviewId, @RequestBody ReviewDTO reviewDTO) {
        ResponseDTO response = reviewService.updateReview(reviewId, reviewDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO> deleteReview(@Valid @PathVariable int reviewId) {
        ResponseDTO response = reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}