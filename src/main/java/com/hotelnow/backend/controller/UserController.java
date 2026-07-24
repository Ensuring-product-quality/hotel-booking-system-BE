package com.hotelnow.backend.controller;

import java.util.Set;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.service.UserService;
import com.hotelnow.backend.util.PageableFactory;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Value("${app.base-url}")
    private String baseUrl;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDTO>>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username,asc") String sort) {

        Pageable pageable = PageableFactory.create(page, size, sort, Set.of("id", "username", "email", "role", "status", "createdAt", "updatedAt"));

        PageResponse<UserResponseDTO> data = userService.searchUsers(role, status, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDetailDTO>> getUserById(@PathVariable Long userId) {
        UserDetailDTO data = userService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        UserResponseDTO data = userService.createUser(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", data, HttpStatus.CREATED.value()));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserResponseDTO data = userService.updateUser(userId, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", data, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("User deleted successfully", null, HttpStatus.NO_CONTENT.value()));
    }

    @PutMapping("/{userId}/avatar")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        String mockAvatarUrl = baseUrl + "/static/avatars/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String savedUrl = userService.uploadAvatar(userId, mockAvatarUrl);
        return ResponseEntity.ok(ApiResponse.success("Avatar uploaded successfully", savedUrl, HttpStatus.OK.value()));
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordDTO) {
        userService.changePassword(userId, changePasswordDTO);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null, HttpStatus.OK.value()));
    }
}
