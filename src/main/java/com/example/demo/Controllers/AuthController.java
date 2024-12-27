package com.example.demo.Controllers;



import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Components.JwtUtils;
import com.example.demo.Entities.PasswordResetToken;
// import com.example.demo.Components.JwtUtils;
// import com.example.demo.DTO.LoginRequest;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.PasswordResetTokenRepository;
import com.example.demo.Repositories.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // private final JwtUtils jwtUtils;
    // public AuthController(JwtUtils jwtUtils) {
    //     this.jwtUtils = jwtUtils;
    // }
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;
    

    public AuthController(PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
    }
    @PostMapping("/register")
    public String register(@Valid @RequestBody Users user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Username already exists";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "Registration successful";
    }

    @PostMapping("/login")
    public String login(@RequestBody Users loginUser) {
        Optional<Users> user = userRepository.findByUsername(loginUser.getUsername());
        if (user.isPresent() && passwordEncoder.matches(loginUser.getPassword(), user.get().getPassword())) {
            return jwtUtils.generateToken(loginUser.getUsername());
        }
        throw new RuntimeException("Invalid credentials");
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        System.out.println("Email received: " + email);
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        resetTokenRepository.save(resetToken);

        // Send token via email (email sending service to be implemented)
        return "Reset link sent to your email";
    }
    @PostMapping("/reset-password/confirm")
    public String confirmResetPassword(@RequestParam String token, @RequestBody String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        // Update user's password
        Users user = resetToken.getUser();
        // PasswordEncoder passwordEncoder = new PasswordEncoder();
        // String hashedpassword = passwordEncoder.encode(newPassword);
        // user.setPassword(hashedpassword); // Encode password if necessary    
        user.setPassword(newPassword);
        userRepository.save(user);

        // Invalidate the token
        resetTokenRepository.delete(resetToken);

        return "Password updated successfully";
    }

}
