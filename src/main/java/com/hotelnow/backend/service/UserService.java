package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.Role;
import com.hotelnow.backend.entity.User;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.BookingRepository;
import com.hotelnow.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public UserService(UserRepository userRepository,
                       BookingRepository bookingRepository,
                       PasswordEncoder passwordEncoder,
                       CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponseDTO> searchUsers(
            String roleValue, String status, String keyword, Pageable pageable) {
        User current = currentUserService.requireCurrentUser();
        if (!currentUserService.isManagement(current)) {
            throw new AccessDeniedException("Only managers and administrators can list users");
        }
        Role role = parseRole(roleValue);
        String normalizedStatus = normalizeStatus(status, true);
        Page<User> page = userRepository.searchUsers(
                role, normalizedStatus, normalize(keyword), pageable);
        return PageResponse.fromPage(page.map(this::mapToUserResponse));
    }

    @Transactional(readOnly = true)
    public UserDetailDTO getUserDetail(Long userId) {
        currentUserService.requireSelfOrStaff(userId);
        User user = findUser(userId);
        List<BookingResponseDTO> bookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getUser().getId().equals(userId))
                .map(booking -> BookingResponseDTO.builder()
                        .id(booking.getId())
                        .userId(userId)
                        .roomId(booking.getRoom().getId())
                        .checkInDate(booking.getCheckInDate())
                        .checkOutDate(booking.getCheckOutDate())
                        .guests(booking.getGuests())
                        .totalPrice(booking.getTotalPrice())
                        .status(booking.getStatus().name().toLowerCase())
                        .build())
                .toList();
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
        User user = User.builder()
                .username(createDTO.getUsername())
                .password(passwordEncoder.encode(createDTO.getPassword()))
                .email(createDTO.getEmail())
                .role(parseRequiredRole(createDTO.getRole()))
                .status(normalizeStatus(createDTO.getStatus(), false))
                .build();
        return mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO updateDTO) {
        currentUserService.requireSelfOrStaff(userId);
        User current = currentUserService.requireCurrentUser();
        User user = findUser(userId);
        if (!user.getEmail().equalsIgnoreCase(updateDTO.getEmail())
                && userRepository.existsByEmail(updateDTO.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
        user.setEmail(updateDTO.getEmail());
        if (currentUserService.isManagement(current)) {
            user.setStatus(normalizeStatus(updateDTO.getStatus(), false));
        }
        return mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User current = currentUserService.requireCurrentUser();
        if (!currentUserService.isAdmin(current)) {
            throw new AccessDeniedException("Only administrators can delete users");
        }
        userRepository.delete(findUser(userId));
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDTO request) {
        currentUserService.requireSelfOrAdmin(userId);
        User user = findUser(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public String uploadAvatar(Long userId, String avatarUrl) {
        currentUserService.requireSelfOrAdmin(userId);
        findUser(userId);
        return avatarUrl;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Role parseRole(String value) {
        return value == null || value.isBlank() ? null : parseRequiredRole(value);
    }

    private Role parseRequiredRole(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (RuntimeException ex) {
            throw new BadRequestException("Invalid role");
        }
    }

    private String normalizeStatus(String value, boolean optional) {
        if (value == null || value.isBlank()) {
            if (optional) {
                return null;
            }
            throw new BadRequestException("Status is required");
        }
        String status = value.trim().toLowerCase();
        if (!status.equals("active") && !status.equals("inactive")) {
            throw new BadRequestException("Status must be active or inactive");
        }
        return status;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
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
