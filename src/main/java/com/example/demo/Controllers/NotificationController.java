package com.example.demo.Controllers;

import com.example.demo.Entities.Notification;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.UserRepository;
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
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
         this.notificationService = notificationService;
         this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
         List<Notification> notifications = notificationService.getAllNotifications();
         return ResponseEntity.ok(notifications);
    }

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
}
