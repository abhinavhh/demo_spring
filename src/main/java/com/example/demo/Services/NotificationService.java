package com.example.demo.Services;

import com.example.demo.Entities.Notification;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(String message, Users user) {
        Notification notification = new Notification(message, user);
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    // New method for user-specific notifications
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
