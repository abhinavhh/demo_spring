package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.Entities.ResetToken;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.ResetTokenRepository;
import com.example.demo.Repositories.UserRepository;

@Service

public class UserService{


    private final UserRepository userRepository ;
    private final ResetTokenRepository resetTokenRepository;

    public UserService(UserRepository userRepository, ResetTokenRepository resetTokenRepository){
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
    }

    public String registerUser(Users user){

        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            return "Username already exists";
        }
        userRepository.save(user);
        return "User Registration successfull";
    }

    public String generatePasswordResetToken(String email) {
        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "User not found";
        }

        ResetToken resetToken = new ResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        resetTokenRepository.save(resetToken);

        return "Password reset token generated successfully. Token: " + resetToken.getToken();
    }

    public String resetPassword(String token, String newPassword) {
        Optional<ResetToken> resetToken = resetTokenRepository.findByToken(token);

        if (resetToken.isEmpty() || resetToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Invalid or expired token";
        }

        Optional<Users> user = userRepository.findByEmail(resetToken.get().getEmail());
        if (user.isPresent()) {
            user.get().setPassword(newPassword);
            userRepository.save(user.get());
            resetTokenRepository.delete(resetToken.get());
            return "Password updated successfully";
        }
        return "User not found";
    }

    public Optional<Users> getUsername(String username){
        return userRepository.findByUsername(username);
    }
}
