package com.example.demo.Services;

import com.example.demo.Entities.SensorData;
import com.example.demo.Entities.UserCrops; // This is your user-crop mapping entity.
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.SensorDataRepository;
import com.example.demo.Repositories.UserCropRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AutomaticIrrigationService {

    private final UserCropRepository userCropRepository;
    private final SensorDataRepository sensorDataRepository;
    private final MosfetControlService mosfetControlService;
    private final NotificationService notificationService;

    public AutomaticIrrigationService(UserCropRepository userCropRepository,
                                      SensorDataRepository sensorDataRepository,
                                      MosfetControlService mosfetControlService,
                                      NotificationService notificationService) {
        this.userCropRepository = userCropRepository;
        this.sensorDataRepository = sensorDataRepository;
        this.mosfetControlService = mosfetControlService;
        this.notificationService = notificationService;
    }

    // Run this scheduled task every minute.
    @Scheduled(fixedRate = 60000)
    public void checkAndIrrigate() {
        // Get all user crop mappings (each mapping includes the crop and user-specific settings)
        List<UserCrops> mappings = userCropRepository.findAll();
        LocalTime now = LocalTime.now();
        
        for (UserCrops mapping : mappings) {
            // Retrieve the user-set irrigation window for this crop
            LocalTime irrigationStart = mapping.getCustomIrrigationStartTime();
            LocalTime irrigationEnd = mapping.getCustomIrrigationEndTime();
            if (irrigationStart == null || irrigationEnd == null) {
                // No irrigation window set; skip this mapping.
                continue;
            }
            
            // Check if the current time falls within the irrigation window.
            // (This simple comparison assumes the window does not span midnight.)
            if (now.isBefore(irrigationStart) || now.isAfter(irrigationEnd)) {
                continue;
            }
            
            // For demonstration, we consider soil moisture for irrigation.
            // Get the latest sensor data for SoilMoisture for this user.
            Long userId = mapping.getUser().getId();
            Optional<SensorData> latestSoilDataOpt = sensorDataRepository
                    .findTopByUserIdAndSensorTypeOrderByIdDesc(userId, "SoilMoisture");
            
            if (!latestSoilDataOpt.isPresent()) {
                continue;
            }
            
            double soilValue = latestSoilDataOpt.get().getValue();
            // Compare against the threshold stored in the mapping.
            double thresholdSoil = mapping.getCustomMaxSoilMoisture(); // or use min if that's desired
            if (soilValue > thresholdSoil) {
                // Trigger the irrigation: switch on MOSFET via ESP32
                try {
                    mosfetControlService.switchOnMosfet();
                } catch (Exception e) {
                    System.err.println("Error switching on MOSFET: " + e.getMessage());
                }
                
                // Create a notification message
                Users current = mapping.getUser();
                String message = "Soil moisture (" + soilValue + "%) for crop " + mapping.getCrop().getName()
                        + " exceeds threshold (" + thresholdSoil + "%). MOSFET activated.";
                notificationService.createNotification(message, current);
            }
        }
    }
}
