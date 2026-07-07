package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookingRepository bookingRepository,
                         HotelRepository hotelRepository,
                         RoomRepository roomRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponseDTO createReview(ReviewCreateDTO createDTO) {
        User user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Hotel hotel = null;
        if (createDTO.getHotelId() != null) {
            hotel = hotelRepository.findById(createDTO.getHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        }

        Room room = null;
        if (createDTO.getRoomId() != null) {
            room = roomRepository.findById(createDTO.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
            if (hotel == null) {
                hotel = room.getHotel();
            }
        }

        if (hotel == null) {
            throw new BadRequestException("Either hotelId or roomId must be provided");
        }

        // Check review eligibility: Check if user has an active/completed booking for this hotel/room
        final Hotel finalHotel = hotel;
        final Room finalRoom = room;
        boolean hasBooking = bookingRepository.findAll().stream()
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .anyMatch(b -> {
                    if (finalRoom != null) {
                        return b.getRoom().getId().equals(finalRoom.getId());
                    }
                    return b.getRoom().getHotel().getId().equals(finalHotel.getId());
                });

        if (!hasBooking) {
            throw new BadRequestException("You can only review hotels or rooms that you have booked and paid for");
        }

        Review review = Review.builder()
                .user(user)
                .hotel(hotel)
                .room(room)
                .rating(createDTO.getRating())
                .comment(createDTO.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // Update hotel average rating
        updateHotelAverageRating(hotel.getId());

        return mapToReviewResponse(savedReview);
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, ReviewUpdateDTO updateDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setRating(updateDTO.getRating());
        review.setComment(updateDTO.getComment());

        Review savedReview = reviewRepository.save(review);

        if (review.getHotel() != null) {
            updateHotelAverageRating(review.getHotel().getId());
        }

        return mapToReviewResponse(savedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        reviewRepository.delete(review);

        if (review.getHotel() != null) {
            updateHotelAverageRating(review.getHotel().getId());
        }
    }

    private void updateHotelAverageRating(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel != null) {
            Double avg = reviewRepository.getAverageRatingForHotel(hotelId);
            hotel.setAverageRating(avg != null ? avg : 0.0);
            hotelRepository.save(hotel);
        }
    }

    private ReviewResponseDTO mapToReviewResponse(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .hotelId(review.getHotel() != null ? review.getHotel().getId() : null)
                .roomId(review.getRoom() != null ? review.getRoom().getId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
