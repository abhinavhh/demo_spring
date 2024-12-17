package com.example.demo.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entities.IrrigationSettings;
import com.example.demo.Services.UserService;

@RestController
@RequestMapping("/api/user")
public abstract class UserController {
    
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/settings")
    public ResponseEntity<String> updateSettings(@RequestBody IrrigationSettings settings){
        userService.updateUserSettings(settings);
        return ResponseEntity.ok("Updates Settings");
    }
}
