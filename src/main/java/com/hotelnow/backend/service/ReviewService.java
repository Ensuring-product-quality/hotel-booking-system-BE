package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.ReviewCreateDTO;
import com.hotelnow.backend.dto.ReviewResponseDTO;
import com.hotelnow.backend.dto.ReviewUpdateDTO;
import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.BookingRepository;
import com.hotelnow.backend.repository.HotelRepository;
import com.hotelnow.backend.repository.ReviewRepository;
import com.hotelnow.backend.repository.RoomRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final CurrentUserService currentUserService;

    public ReviewService(ReviewRepository reviewRepository,
                         BookingRepository bookingRepository,
                         HotelRepository hotelRepository,
                         RoomRepository roomRepository,
                         CurrentUserService currentUserService) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ReviewResponseDTO createReview(ReviewCreateDTO createDTO) {
        User user = currentUserService.requireCurrentUser();
        Hotel hotel = createDTO.getHotelId() == null ? null
                : hotelRepository.findById(createDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        Room room = createDTO.getRoomId() == null ? null
                : roomRepository.findById(createDTO.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (room != null && hotel == null) {
            hotel = room.getHotel();
        }
        if (hotel == null) {
            throw new BadRequestException("Either hotelId or roomId must be provided");
        }

        Hotel targetHotel = hotel;
        Room targetRoom = room;
        boolean eligible = bookingRepository.findAll().stream()
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED
                        || booking.getStatus() == BookingStatus.COMPLETED)
                .filter(booking -> booking.getCheckOutDate().isBefore(LocalDate.now()))
                .anyMatch(booking -> targetRoom != null
                        ? booking.getRoom().getId().equals(targetRoom.getId())
                        : booking.getRoom().getHotel().getId().equals(targetHotel.getId()));
        if (!eligible) {
            throw new BadRequestException("You can only review after completing a stay");
        }

        Review saved = reviewRepository.save(Review.builder()
                .user(user)
                .hotel(hotel)
                .room(room)
                .rating(createDTO.getRating())
                .comment(createDTO.getComment())
                .build());
        updateHotelAverageRating(hotel.getId());
        return mapToReviewResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviews(Long hotelId, Long roomId) {
        List<Review> reviews;
        if (roomId != null) {
            reviews = reviewRepository.findByRoomId(roomId);
        } else if (hotelId != null) {
            reviews = reviewRepository.findByHotelId(hotelId);
        } else {
            throw new BadRequestException("hotelId or roomId is required");
        }
        return reviews.stream().map(this::mapToReviewResponse).toList();
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, ReviewUpdateDTO updateDTO) {
        Review review = findOwnedReview(reviewId);
        review.setRating(updateDTO.getRating());
        review.setComment(updateDTO.getComment());
        Review saved = reviewRepository.save(review);
        if (review.getHotel() != null) {
            updateHotelAverageRating(review.getHotel().getId());
        }
        return mapToReviewResponse(saved);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = findOwnedReview(reviewId);
        Long hotelId = review.getHotel() == null ? null : review.getHotel().getId();
        reviewRepository.delete(review);
        if (hotelId != null) {
            updateHotelAverageRating(hotelId);
        }
    }

    private Review findOwnedReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        User current = currentUserService.requireCurrentUser();
        if (!review.getUser().getId().equals(current.getId()) && !currentUserService.isAdmin(current)) {
            throw new AccessDeniedException("You cannot modify another user's review");
        }
        return review;
    }

    private void updateHotelAverageRating(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel != null) {
            Double average = reviewRepository.getAverageRatingForHotel(hotelId);
            hotel.setAverageRating(average == null ? 0.0 : average);
            hotelRepository.save(hotel);
        }
    }

    private ReviewResponseDTO mapToReviewResponse(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .hotelId(review.getHotel() == null ? null : review.getHotel().getId())
                .roomId(review.getRoom() == null ? null : review.getRoom().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
