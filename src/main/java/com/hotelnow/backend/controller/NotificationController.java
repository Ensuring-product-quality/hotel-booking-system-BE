package com.hotelnow.backend.controller;

import java.util.Set;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.security.UserPrincipal;
import com.hotelnow.backend.service.NotificationService;
import com.hotelnow.backend.util.PageableFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponseDTO>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        Pageable pageable = PageableFactory.create(page, size, sort, Set.of("id", "status", "createdAt"));

        PageResponse<NotificationResponseDTO> data = notificationService.getNotifications(userPrincipal.getId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null, HttpStatus.OK.value()));
    }
}
