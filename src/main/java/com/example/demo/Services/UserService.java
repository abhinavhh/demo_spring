package com.example.demo.Services;

import com.example.demo.Entities.IrrigationSettings;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("The username is : "+username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateUserSettings(IrrigationSettings settings) {
        Users currentUser = getCurrentUser();
    
        // Update profile details from the associated user of the IrrigationSettings
        if (settings.getUser().getUsername() != null) {
            currentUser.setUsername(settings.getUser().getUsername());
        }
        if (settings.getUser().getEmail() != null) {
            currentUser.setEmail(settings.getUser().getEmail());
        }
    
        // Save changes to the database
        userRepository.save(currentUser);
    }
    

    public void updateName(String newName) {
        Users user = getCurrentUser();
        user.setUsername(newName);
        userRepository.save(user);
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        Users user = getCurrentUser();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void deleteCurrentUser() {
        Users user = getCurrentUser();
        userRepository.delete(user);
    }
}
