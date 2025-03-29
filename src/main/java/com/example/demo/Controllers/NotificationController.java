package com.example.demo.Controllers;

import com.example.demo.Entities.Notification;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.NotificationService;
import com.example.demo.Services.SensorDataService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final SensorDataService sensorDataService;
    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationService notificationService,
                                  UserRepository userRepository,
                                  SensorDataService sensorDataService,
                                  NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.sensorDataService = sensorDataService;
        this.notificationRepository = notificationRepository;
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId
    ) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Ensure the notification belongs to the user
        if (!notification.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }

    // GET endpoint for fetching notifications by userId
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // POST endpoint to create a new notification manually
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String userIdStr = payload.get("userId");
        if (userIdStr == null) {
            return ResponseEntity.badRequest().build();
        }
        Long userId = Long.valueOf(userIdStr);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = notificationService.createNotification(message, user);
        return ResponseEntity.ok(notification);
    }

    // New endpoint to check sensor data and notify the user based on latest sensor values
    @PostMapping("/check")
    public ResponseEntity<List<Notification>> checkSensorAndNotify(@RequestParam Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Call service layer to check sensor data and notify if thresholds are breached
        notificationService.checkLatestSensorDataAndNotify(user, sensorDataService);
        // Return the updated list of notifications for the user
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
}
