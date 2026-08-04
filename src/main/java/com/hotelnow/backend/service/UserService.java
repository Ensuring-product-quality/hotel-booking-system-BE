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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final FileStorageService fileStorageService;

    public UserService(UserRepository userRepository,
                       BookingRepository bookingRepository,
                       PasswordEncoder passwordEncoder,
                       CurrentUserService currentUserService,
                       FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
        this.fileStorageService = fileStorageService;
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
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .bookings(bookings)
                .build();
    }

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO createDTO) {
        if (userRepository.existsByUsername(createDTO.getUsername())) {
            throw new BadRequestException("Tên đăng nhập này đã được sử dụng");
        }
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new BadRequestException("Email này đã được đăng ký tài khoản");
        }
        User user = User.builder()
                .username(createDTO.getUsername())
                .password(passwordEncoder.encode(createDTO.getPassword()))
                .email(createDTO.getEmail())
                .fullName(createDTO.getFullName())
                .phone(createDTO.getPhone())
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
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) {
            if (!user.getEmail().equalsIgnoreCase(updateDTO.getEmail())
                    && userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new BadRequestException("Email này đã được sử dụng bởi tài khoản khác");
            }
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (currentUserService.isManagement(current)) {
            if (updateDTO.getStatus() != null && !updateDTO.getStatus().isBlank()) {
                user.setStatus(normalizeStatus(updateDTO.getStatus(), false));
            }
            if (updateDTO.getRole() != null && !updateDTO.getRole().isBlank()) {
                user.setRole(parseRequiredRole(updateDTO.getRole()));
            }
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
            throw new BadRequestException("Mật khẩu cũ không chính xác");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public String uploadAvatar(Long userId, MultipartFile file) {
        currentUserService.requireSelfOrAdmin(userId);
        User user = findUser(userId);
        String avatarUrl = fileStorageService.storeImage(file, "avatars");
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private Role parseRole(String value) {
        return value == null || value.isBlank() ? null : parseRequiredRole(value);
    }

    private Role parseRequiredRole(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (RuntimeException ex) {
            throw new BadRequestException("Vai trò người dùng không hợp lệ");
        }
    }

    private String normalizeStatus(String value, boolean optional) {
        if (value == null || value.isBlank()) {
            if (optional) {
                return null;
            }
            throw new BadRequestException("Trạng thái không được để trống");
        }
        String status = value.trim().toLowerCase();
        if (!status.equals("active") && !status.equals("inactive")) {
            throw new BadRequestException("Trạng thái phải là active hoặc inactive");
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
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
