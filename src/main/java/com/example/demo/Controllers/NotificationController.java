package com.example.demo.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entities.Notifications;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.Repositories.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private Users getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @GetMapping
    public List<Notifications> getUserNotifications() {
        Users user = getCurrentUser();
        return notificationRepository.findByUserIdOrderByTimestampDesc(user.getId());
    }
}
