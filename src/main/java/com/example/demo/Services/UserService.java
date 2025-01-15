package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Users;
import com.example.demo.Repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JavaMailSender emailSender;
    private final Map<String, OTPData> otpStorage = new ConcurrentHashMap<>();
    private static final int OTP_EXPIRY_MINUTES = 5; // Set as constant instead of using @Value

    // Inner class for OTP data
    private static class OTPData {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OTPData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }

    public UserService(UserRepository userRepository, JavaMailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    public String registerUser(Users user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Username already exists";
        }
        userRepository.save(user);
        return "User Registration successful";
    }

    public Optional<Users> getUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String updateUserProfile(String currentUsername, Users updatedUser) {
        Optional<Users> existingUser = userRepository.findByUsername(currentUsername);
        if (existingUser.isEmpty()) {
            return "User not found";
        }

        Users user = existingUser.get();

        // Check if new username is different and available
        if (!currentUsername.equals(updatedUser.getUsername())) {
            if (userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                return "Username already taken";
            }
            user.setUsername(updatedUser.getUsername());
        }

        // Check if email is being updated
        if (!user.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                return "Email already in use";
            }
            user.setEmail(updatedUser.getEmail());
        }

        userRepository.save(user);
        return "Profile updated successfully";
    }

    public String generateOTP(String email) {
        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "User not found";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStorage.put(email, new OTPData(otp, expiryTime));

        // Here you would typically send the OTP via email
        // For now, we'll just return it
        return "OTP generated: " + otp;
    }

    public boolean verifyOTP(String email, String otp) {
        OTPData otpData = otpStorage.get(email);
        if (otpData == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(otpData.getExpiryTime())) {
            otpStorage.remove(email);
            return false;
        }

        return otpData.getOtp().equals(otp);
    }

    public String resetPassword(String email, String otp, String newPassword) {
        if (!verifyOTP(email, otp)) {
            return "Invalid or expired OTP";
        }

        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "User not found";
        }

        user.get().setPassword(newPassword);
        userRepository.save(user.get());
        otpStorage.remove(email);
        return "Password updated successfully";
    }
    public String generateAndSendOTP(String email) {
        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "User not found";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStorage.put(email, new OTPData(otp, expiryTime));

        // Send OTP via email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP for password reset is: " + otp + 
                          "\nThis OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes.");
            emailSender.send(message);
            return "OTP sent successfully to your email";
        } catch (Exception e) {
            otpStorage.remove(email); // Clean up OTP if email sending fails
            return "Failed to send OTP: " + e.getMessage();
        }
    }
}