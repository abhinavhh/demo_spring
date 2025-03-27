package com.example.demo.Controllers;


import com.example.demo.Services.IrrigationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/irrigation")
public class IrrigationController {

    private final IrrigationService irrigationService;

    public IrrigationController(IrrigationService irrigationService) {
        this.irrigationService = irrigationService;
    }

    /**
     * Set irrigation timing for a crop for a specific user.
     */
    @PostMapping("/set-timing/{cropId}")
    public ResponseEntity<String> setIrrigationTiming(
            @PathVariable Long cropId,
            @RequestParam Long userId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        irrigationService.setIrrigationTiming(userId, cropId, startTime, endTime);
        return ResponseEntity.ok("Irrigation timing set successfully for crop: " + cropId);
    }

    @GetMapping("/get-timing/{cropId}")
    public ResponseEntity<String> getIrrigationTiming(
            @PathVariable Long cropId,
            @RequestParam Long userId) {
        String timingInfo = irrigationService.getIrrigationTiming(userId, cropId);
        return ResponseEntity.ok(timingInfo);
    }

    @PostMapping("/analyze/{cropId}")
    public ResponseEntity<String> analyzeAndControlIrrigation(
            @PathVariable Long cropId,
            @RequestParam Long userId) {
        String result = irrigationService.analyzeAndControlIrrigation(userId, cropId);
        return ResponseEntity.ok(result);
    }

    /**
     * Manual control of the irrigation valve for a specific user and crop.
     */
    @PostMapping("/manual-control")
    public ResponseEntity<String> manualValveControl(
            @RequestParam Long userId,
            @RequestParam Long cropId,
            @RequestParam boolean openValve) {
        irrigationService.manualValveControl(userId, cropId, openValve);
        return ResponseEntity.ok(openValve ? "Valve opened manually" : "Valve closed manually.");
    }

    /**
     * Fetch the current irrigation status for a specific user and crop.
     */
    @GetMapping("/status")
    public ResponseEntity<String> getIrrigationStatus(
            @RequestParam Long userId,
            @RequestParam Long cropId) {
        return ResponseEntity.ok(irrigationService.getCurrentStatus(userId, cropId));
    }
    @PutMapping("/automate")
    public ResponseEntity<String> automateValveControl(
            @RequestParam Long userId,
            @RequestParam Long cropId) {
        String result = irrigationService.automaticValveControl(userId, cropId);
        return ResponseEntity.ok(result);
    }
}
