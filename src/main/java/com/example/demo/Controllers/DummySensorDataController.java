package com.example.demo.Controllers;

import com.example.demo.Services.DummySensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class DummySensorDataController {

    private final DummySensorDataService dummySensorDataService;

    // Generate dummy data for a specific user
    @PostMapping("/dummy/{userId}")
    public ResponseEntity<String> generateDummyData(@PathVariable Long userId) {
        System.out.println(userId);
        dummySensorDataService.generateDummyData(userId);
        return ResponseEntity.ok("✅ Dummy data generated for user: " + userId);
    }
}
