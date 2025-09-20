package com.example.demo.Controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entities.Users;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.UserService;


@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService){
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Users> getUserProfile(@PathVariable Long userId) {
        
        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, String> userDetails, @RequestParam("currentUsername") String currentUsername) {
        Optional<Users> existingUser = userRepository.findByUsername(currentUsername);
        if (existingUser.isPresent()) {
            Users user = existingUser.get();
            user.setUsername(userDetails.get("username"));
            user.setEmail(userDetails.get("email"));
            Users savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        try {
            String result = userService.generateAndSendOTP(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        try {
            boolean isValid = userService.verifyOTP(email, otp);
            if (isValid) {
                return ResponseEntity.ok("OTP verified successfully");
            }
            return ResponseEntity.badRequest().body("Invalid OTP");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        try {
            String result = userService.resetPassword(email, otp, newPassword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Optional: You can add endpoints here to expose user-specific crop selections or sensor data if needed.
}
