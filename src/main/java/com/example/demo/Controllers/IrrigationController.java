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
     * ✅ Set Irrigation Timing for a Crop
     */
    @PostMapping("/set-timing")
    public ResponseEntity<String> setIrrigationTiming(@RequestBody Long cropId) {
        irrigationService.setIrrigationTiming(cropId);
        return ResponseEntity.ok("Irrigation timing set successfully for crop: " + cropId);
    }

    /**
     * ✅ Automatic Irrigation Based on Real-Time Data
     */
    @PostMapping("/analyze/{cropId}")
    public ResponseEntity<String> analyzeAndControlIrrigation(@PathVariable Long cropId) {
        irrigationService.analyzeAndControlIrrigation(cropId);
        return ResponseEntity.ok("Irrigation analysis completed for crop ID: " + cropId);
    }

    /**
     * ✅ Manual Control of Irrigation Valve
     */
    @PostMapping("/manual-control")
    public ResponseEntity<String> manualValveControl(@RequestParam boolean openValve) {
        irrigationService.manualValveControl(openValve);
        return ResponseEntity.ok(openValve ? "Valve opened manually" : "Valve closed manually.");
    }

    /**
     * ✅ Fetch Current Irrigation Status
     */
    @GetMapping("/status")
    public ResponseEntity<String> getIrrigationStatus() {
        return ResponseEntity.ok(irrigationService.getCurrentStatus());
    }
}