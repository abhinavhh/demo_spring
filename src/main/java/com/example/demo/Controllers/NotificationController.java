package com.example.demo.Controllers;

import com.example.demo.Entities.Notification;
import com.example.demo.Services.NotificationService;
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

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
         List<Notification> notifications = notificationService.getAllNotifications();
         return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Map<String, String> payload) {
         String message = payload.get("message");
         Notification notification = notificationService.createNotification(message);
         return ResponseEntity.ok(notification);
    }
}
