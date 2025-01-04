package com.example.demo.Controllers;



import java.util.Optional;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.Entities.Users;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.UserService;

@RestController
@RequestMapping("/api/user")
public abstract class UserController {
    
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService){
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<Users> getUserProfile(@PathVariable String username) {
        Optional<Users> user = userService.getUsername(username);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/profile/update")
    public ResponseEntity<String> updateUserProfile(@RequestBody Users updatedUser) {
        Optional<Users> existingUser = userRepository.findByUsername(updatedUser.getUsername());
        if (existingUser.isPresent()) {
            Users user = existingUser.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            userRepository.save(user);
            return ResponseEntity.ok("User details updated successfully.");
        }
        return ResponseEntity.notFound().build();
    }
}