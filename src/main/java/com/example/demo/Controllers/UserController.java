package com.example.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entities.IrrigationSettings;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.UserService;

@RestController
@RequestMapping("/api/user")
public abstract class UserController {
    
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }
    @PostMapping("/settings")
    public ResponseEntity<String> updateSettings(@RequestBody IrrigationSettings settings){
        userService.updateUserSettings(settings);
        return ResponseEntity.ok("Updated Settings");
    }
}