package com.example.demo.Controllers;

import com.example.demo.Components.NotificationTimer;
import com.example.demo.Entities.Notification;
import com.example.demo.Services.NotificationService;
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

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET /api/notifications?userId={userId}
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // PATCH /api/notifications/{notificationId}/read
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId,
                                           @RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            boolean updated = notificationService.markNotificationAsRead(notificationId, userId);
            if (updated) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            // Optionally log the error
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // POST /api/notifications/check?userId={userId}
    // This endpoint triggers the sensor data check for the given user and returns updated notifications.
    @PostMapping("/check")
    public ResponseEntity<List<Notification>> checkNotifications(@RequestParam Long userId) {
        List<Notification> notifications = NotificationTimer.startNewTimer(notificationService, userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
}
