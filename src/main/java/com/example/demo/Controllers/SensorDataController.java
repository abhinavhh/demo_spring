package com.example.demo.Controllers;

import com.example.demo.Entities.SensorData;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.SensorDataRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorDataController {

    @Autowired
    private SensorDataRepository sensorDataRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    private Users getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @GetMapping("/latest")
    public List<SensorData> getLatestSensorData() {
        Users user = getCurrentUser();
        return sensorDataRepository.findByUserIdAndSensorTypeOrderByTimestampDesc(user.getId(), "temperature");
    }

    @GetMapping("/{type}")
    public List<SensorData> getSensorDataByType(
        @PathVariable String type,
        @RequestParam String range
    ) {
        Users user = getCurrentUser();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;

        switch (range.toLowerCase()) {
            case "day":
                start = end.minus(1, ChronoUnit.DAYS);
                break;
            case "week":
                start = end.minus(1, ChronoUnit.WEEKS);
                break;
            case "month":
                start = end.minus(1, ChronoUnit.MONTHS);
                break;
            default:
                throw new IllegalArgumentException("Invalid range: " + range);
        }

        return sensorDataRepository.findByUserIdAndSensorTypeAndTimestampBetween(
            user.getId(), type, start, end
        );
    }
    @PostMapping
    public String addSensorData(@RequestBody SensorData sensorData) {
        Users user = getCurrentUser();
        sensorData.setUser(user);
        sensorData.setTimestamp(LocalDateTime.now());
        sensorDataRepository.save(sensorData);

        // Analyze sensor data for notifications
        notificationService.analyzeSensorData(user);

        return "Sensor data added successfully";
    }
}
