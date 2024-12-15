package com.example.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;;

@RestController
public class HomeController {

    @PostMapping("/")
    public String home() {
        return "Welcome to the Smart Irrigation Application!";
    }
}
