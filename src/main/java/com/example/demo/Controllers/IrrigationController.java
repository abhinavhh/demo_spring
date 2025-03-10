package com.example.demo.Controllers;

import com.example.demo.Services.IrrigationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "https://smart-irrigation-rho.vercel.app")
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
    @PostMapping("/set-timing/{cropId}")
    public ResponseEntity<String> setIrrigationTiming(@PathVariable Long cropId, @RequestParam String startTime, @RequestParam String endTime) {
        irrigationService.setIrrigationTiming(cropId, startTime, endTime);
        return ResponseEntity.ok("Irrigation timing set successfully for crop: " + cropId);
    }
    @GetMapping("/get-timing/{cropId}")
    public ResponseEntity<String> getIrrigationTiming(@PathVariable Long cropId) {
        String timingInfo = irrigationService.getIrrigationTiming(cropId);
        return ResponseEntity.ok(timingInfo);
    }

    @PostMapping("/analyze/{cropId}")
    public ResponseEntity<String> analyzeAndControlIrrigation(@PathVariable Long cropId) {
        String result = irrigationService.analyzeAndControlIrrigation(cropId);
        return ResponseEntity.ok(result);
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