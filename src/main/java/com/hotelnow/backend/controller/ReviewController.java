package com.hotelnow.backend.controller;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReview(@Valid @RequestBody ReviewCreateDTO createDTO) {
        ReviewResponseDTO data = reviewService.createReview(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review posted successfully", data, HttpStatus.CREATED.value()));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateDTO updateDTO) {
        ReviewResponseDTO data = reviewService.updateReview(reviewId, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", data, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Review deleted successfully", null, HttpStatus.NO_CONTENT.value()));
    }
}
