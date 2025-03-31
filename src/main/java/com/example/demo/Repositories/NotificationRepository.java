package com.example.demo.Repositories;

import com.example.demo.Entities.Notification;

import com.example.demo.Entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserAndMessageAndCreatedAtAfter(Users user, String message, LocalDateTime oneHourAgo);
}
