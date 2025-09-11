package com.example.demo.Controllers;

import com.example.demo.Entities.Crops;
import com.example.demo.Entities.UserCrops;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.CropRepository;
import com.example.demo.Repositories.UserCropRepository;
import com.example.demo.Repositories.UserRepository;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usercrops")
public class UserCropController {

    private final UserCropRepository userCropRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository; // Assumes this repository exists

    public UserCropController(UserCropRepository userCropRepository, CropRepository cropRepository, UserRepository userRepository) {
        this.userCropRepository = userCropRepository;
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
    }
    
    // Endpoint for a user to select a crop from the global list.
    // In a real application, you would get the authenticated user instead of passing userId.
    @PostMapping("/select")
    public ResponseEntity<String> selectCrop(@RequestParam Long userId, @RequestParam Long cropId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Crops crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        // Ensure the user hasn't already selected this crop.
        if (userCropRepository.findByUserIdAndCropId(userId, cropId).isPresent()){
            return ResponseEntity.badRequest().body("Crop already selected for user");
        }

        UserCrops userCrop = new UserCrops();
        userCrop.setUser(user);
        userCrop.setCrop(crop);

        // Initialize user-specific settings with global defaults
        userCrop.setCustomMinTemperature(crop.getMinTemperature());
        userCrop.setCustomMaxTemperature(crop.getMaxTemperature());
        userCrop.setCustomMinHumidity(crop.getMinHumidity());
        userCrop.setCustomMaxHumidity(crop.getMaxHumidity());
        userCrop.setCustomMinSoilMoisture(crop.getMinSoilMoisture());
        userCrop.setCustomMaxSoilMoisture(crop.getMaxSoilMoisture());
        userCrop.setCustomIrrigationStartTime(crop.getIrrigationStartTime());
        userCrop.setCustomIrrigationEndTime(crop.getIrrigationEndTime());
        userCrop.setManualCloseEnabled(false);
        userCrop.setManualOpenEnabled(false);
        
        userCropRepository.save(userCrop);
        return ResponseEntity.ok("Crop selected successfully for user!");
    }
    
    // Endpoint for updating a user's crop settings.
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUserCrop(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        UserCrops userCrop = userCropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserCrop mapping not found"));
        
        // Update irrigation times if provided.
        if (updates.containsKey("customIrrigationStartTime") && updates.containsKey("customIrrigationEndTime")) {
            String startTime = updates.get("customIrrigationStartTime");
            String endTime = updates.get("customIrrigationEndTime");
            try {
                userCrop.setCustomIrrigationStartTime(LocalTime.parse(startTime));
                userCrop.setCustomIrrigationEndTime(LocalTime.parse(endTime));
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body("Invalid time format. Please use HH:mm.");
            }
        }
        
        if (updates.containsKey("customMinTemperature")) {
            userCrop.setCustomMinTemperature(Double.valueOf(updates.get("customMinTemperature")));
        }
        if (updates.containsKey("customMaxTemperature")) {
            userCrop.setCustomMaxTemperature(Double.valueOf(updates.get("customMaxTemperature")));
        }
        if (updates.containsKey("customMinHumidity")) {
            userCrop.setCustomMinHumidity(Double.valueOf(updates.get("customMinHumidity")));
        }
        if (updates.containsKey("customMaxHumidity")) {
            userCrop.setCustomMaxHumidity(Double.valueOf(updates.get("customMaxHumidity")));
        }
        if (updates.containsKey("customMinSoilMoisture")) {
            userCrop.setCustomMinSoilMoisture(Double.valueOf(updates.get("customMinSoilMoisture")));
        }
        if (updates.containsKey("customMaxSoilMoisture")) {
            userCrop.setCustomMaxSoilMoisture(Double.valueOf(updates.get("customMaxSoilMoisture")));
        }
        
        userCropRepository.save(userCrop);
        return ResponseEntity.ok("User crop settings updated successfully!");
    }
    
    // Endpoint to retrieve all crops selected by a given user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserCrops>> getUserCrops(@PathVariable Long userId) {
        List<UserCrops> userCrops = userCropRepository.findByUserId(userId);
        return ResponseEntity.ok(userCrops);
    }

    @DeleteMapping("/deselect")
    public ResponseEntity<String> deselectCrop(@RequestParam Long userId, @RequestParam Long cropId) {
        Optional<UserCrops> mappingOpt = userCropRepository.findByUserIdAndCropId(userId, cropId);
        if(mappingOpt.isPresent()){
            userCropRepository.delete(mappingOpt.get());
            return ResponseEntity.ok("Crop deselected successfully for user!");
        } else {
            return ResponseEntity.badRequest().body("Mapping not found.");
        }
    }
    
    // New endpoint to fetch the user-set threshold values.
    // Optionally accepts a cropId to specify which crop's thresholds to fetch.
    @GetMapping("/thresholds")
    public ResponseEntity<?> getUserThresholds(@RequestParam Long userId, @RequestParam(required = false) Long cropId) {
        Optional<UserCrops> mappingOpt;
        if (cropId != null) {
            mappingOpt = userCropRepository.findByUserIdAndCropId(userId, cropId);
        } else {
            List<UserCrops> userCrops = userCropRepository.findByUserId(userId);
            if (userCrops.isEmpty()) {
                return ResponseEntity.badRequest().body("No crops selected for user.");
            }
            mappingOpt = Optional.of(userCrops.get(0));
        }
        
        if (!mappingOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Mapping not found.");
        }
        
        UserCrops userCrop = mappingOpt.get();
        Map<String, Object> thresholds = new HashMap<>();
        thresholds.put("customMinTemperature", userCrop.getCustomMinTemperature());
        thresholds.put("customMaxTemperature", userCrop.getCustomMaxTemperature());
        thresholds.put("customMinHumidity", userCrop.getCustomMinHumidity());
        thresholds.put("customMaxHumidity", userCrop.getCustomMaxHumidity());
        thresholds.put("customMinSoilMoisture", userCrop.getCustomMinSoilMoisture());
        thresholds.put("customMaxSoilMoisture", userCrop.getCustomMaxSoilMoisture());
        thresholds.put("customIrrigationStartTime", userCrop.getCustomIrrigationStartTime());
        thresholds.put("customIrrigationEndTime", userCrop.getCustomIrrigationEndTime());
        
        return ResponseEntity.ok(thresholds);
    }
}
