package com.hotelnow.backend.repository;

import com.hotelnow.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND (:status IS NULL OR n.status = :status)")
    Page<Notification> findByUserIdAndStatus(@Param("userId") Long userId,
                                             @Param("status") String status,
                                             Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'read' " +
            "WHERE n.user.id = :userId AND n.status = 'unread'")
    int markAllAsReadByUserId(@Param("userId") Long userId);
}
