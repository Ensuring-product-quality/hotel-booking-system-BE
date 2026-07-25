package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.NotificationResponseDTO;
import com.hotelnow.backend.dto.PageResponse;
import com.hotelnow.backend.entity.Notification;
import com.hotelnow.backend.entity.User;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    public NotificationService(NotificationRepository notificationRepository,
                               CurrentUserService currentUserService) {
        this.notificationRepository = notificationRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponseDTO> getNotifications(
            Long userId, String status, Pageable pageable) {
        User current = currentUserService.requireCurrentUser();
        if (!current.getId().equals(userId)) {
            throw new AccessDeniedException("You cannot access another user's notifications");
        }
        Page<Notification> page =
                notificationRepository.findByUserIdAndStatus(userId, status, pageable);
        return PageResponse.fromPage(page.map(this::mapToNotificationResponse));
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        User current = currentUserService.requireCurrentUser();
        if (!notification.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("You cannot modify another user's notification");
        }
        notification.setStatus("read");
        notificationRepository.save(notification);
    }

    @Transactional
    public int markAllAsRead() {
        User current = currentUserService.requireCurrentUser();
        return notificationRepository.markAllAsReadByUserId(current.getId());
    }

    private NotificationResponseDTO mapToNotificationResponse(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
