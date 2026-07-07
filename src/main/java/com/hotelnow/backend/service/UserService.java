package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.BookingRepository;
import com.hotelnow.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BookingRepository bookingRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponseDTO> searchUsers(String role, String status, String keyword, Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PageResponse.fromPage(page.map(this::mapToUserResponse));
    }

    @Transactional(readOnly = true)
    public UserDetailDTO getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<BookingResponseDTO> bookings = bookingRepository.findAll().stream()
                .filter(b -> b.getUser().getId().equals(userId))
                .map(b -> BookingResponseDTO.builder()
                        .id(b.getId())
                        .userId(userId)
                        .roomId(b.getRoom().getId())
                        .checkInDate(b.getCheckInDate())
                        .checkOutDate(b.getCheckOutDate())
                        .guests(b.getGuests())
                        .totalPrice(b.getTotalPrice())
                        .status(b.getStatus().name().toLowerCase())
                        .build())
                .collect(Collectors.toList());

        return UserDetailDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus())
                .bookings(bookings)
                .build();
    }

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO createDTO) {
        if (userRepository.existsByUsername(createDTO.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role role;
        try {
            role = Role.valueOf(createDTO.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role");
        }

        User user = User.builder()
                .username(createDTO.getUsername())
                .password(passwordEncoder.encode(createDTO.getPassword()))
                .email(createDTO.getEmail())
                .role(role)
                .status(createDTO.getStatus())
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equalsIgnoreCase(updateDTO.getEmail()) && userRepository.existsByEmail(updateDTO.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        user.setEmail(updateDTO.getEmail());
        user.setStatus(updateDTO.getStatus());

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDTO changePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public String uploadAvatar(Long userId, String avatarUrl) {
        return avatarUrl;
    }

    private UserResponseDTO mapToUserResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus())
                .build();
    }
}
