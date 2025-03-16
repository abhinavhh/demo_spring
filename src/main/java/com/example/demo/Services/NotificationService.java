package com.example.demo.Services;

import com.example.demo.Entities.Notification;
import com.example.demo.Repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(String message) {
        Notification notification = new Notification(message, null);
        // Optionally, add additional logic (e.g., avoid duplicates)
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
