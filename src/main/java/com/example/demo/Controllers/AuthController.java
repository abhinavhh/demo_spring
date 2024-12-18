package com.example.demo.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Components.JwtUtils;
import com.example.demo.DTO.LoginRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final JwtUtils jwtUtils;
    
    public AuthController(JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        String username = loginRequest.username();
        String password = loginRequest.password();
        if("user".equals(username) && "password".equals(password)){
            
            String token = jwtUtils.generateToken(username);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Invalid Credentials");
    }
}
