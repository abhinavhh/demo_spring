package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.example.demo.Components.JwtUtils;
import com.example.demo.DTO.AuthResponse;
import com.example.demo.DTO.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Entities.Users;
import com.example.demo.Repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JavaMailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, OTPData> otpStorage = new ConcurrentHashMap<>();
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
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

//    public UserService(UserRepository userRepository, JavaMailSender emailSender, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.emailSender = emailSender;
//        this.passwordEncoder = passwordEncoder;
//    }

    public String register(UserDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already exists";
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists";
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .role("USER")
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtils.generateToken(username, user.getId(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(username);

        return new AuthResponse(accessToken, refreshToken, user.getId());
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

        if (!currentUsername.equals(updatedUser.getUsername())) {
            if (userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                return "Username already taken";
            }
            user.setUsername(updatedUser.getUsername());
        }

        if (!user.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                return "Email already in use";
            }
            user.setEmail(updatedUser.getEmail());
        }

        userRepository.save(user);
        return "Profile updated successfully";
    }
    
    @Autowired
    private EmailService emailService;
    
    public String generateOTP(String email) {
        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "User not found";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStorage.put(email, new OTPData(otp, expiryTime));
        
        String subject = "Password Reset OTP Mail";
        emailService.sendOTP(email, otp, subject);
        return "OTP sent to your email successfully";
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
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP for password reset is: " + otp + 
                          "\nThis OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes.");
            emailSender.send(message);
            return "OTP sent successfully to your email";
        } catch (Exception e) {
            otpStorage.remove(email);
            return "Failed to send OTP: " + e.getMessage();
        }
    }
}
