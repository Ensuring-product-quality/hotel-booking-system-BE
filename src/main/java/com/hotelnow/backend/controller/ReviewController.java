package com.hotelnow.backend.controller;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getReviews(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviews(hotelId, roomId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReview(
            @Valid @RequestBody ReviewCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Review posted successfully",
                        reviewService.createReview(createDTO),
                        HttpStatus.CREATED.value()));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateDTO updateDTO) {
        return ResponseEntity.ok(ApiResponse.success(
                "Review updated successfully",
                reviewService.updateReview(reviewId, updateDTO),
                HttpStatus.OK.value()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
