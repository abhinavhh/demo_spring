package com.example.demo.Services;


import com.example.demo.Entities.Crops;
import com.example.demo.Entities.IrrigationSettings;
import com.example.demo.Entities.UserCrops;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.CropRepository;
import com.example.demo.Repositories.IrrigationSettingsRepository;
import com.example.demo.Repositories.UserCropRepository;
import com.example.demo.Repositories.UserRepository;


import org.springframework.stereotype.Service;


import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class IrrigationService {

    private final CropRepository cropRepository;
    private final IrrigationSettingsRepository irrigationSettingsRepository;
    private final UserRepository userRepository;
    private final UserCropRepository userCropRepository;
    private final MosfetControlService mosfetControlService;
    private final SensorDataRepository sensorDataRepository;

    public IrrigationService(CropRepository cropRepository,
                             IrrigationSettingsRepository irrigationSettingsRepository,
                             UserRepository userRepository,
                             UserCropRepository userCropRepository,
                             MosfetControlService mosfetControlService,
                             SensorDataRepository sensorDataRepository) {
        this.cropRepository = cropRepository;
        this.irrigationSettingsRepository = irrigationSettingsRepository;
        this.userRepository = userRepository;
        this.userCropRepository = userCropRepository;
        this.mosfetControlService = mosfetControlService;
        this.sensorDataRepository = sensorDataRepository;
    }

    // Set irrigation timing for a crop for a specific user.
    public void setIrrigationTiming(Long userId, Long cropId, String startTime, String endTime) {
        Optional<Crops> optionalCrop = cropRepository.findById(cropId);
        if (optionalCrop.isEmpty()) {
            throw new RuntimeException("Crop not found!");
        }
        Crops crop = optionalCrop.get();

        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        Optional<IrrigationSettings> optionalSettings = irrigationSettingsRepository.findByCropIdAndUserId(cropId, userId);
        IrrigationSettings settings;
        if (optionalSettings.isPresent()) {
            settings = optionalSettings.get();
            settings.setStartTime(start);
            settings.setEndTime(end);
        } else {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            settings = new IrrigationSettings(crop, start, end, false, user);
        }
        irrigationSettingsRepository.save(settings);
    }

    // Retrieve irrigation timing for a specific user and crop.
    public String getIrrigationTiming(Long userId, Long cropId) {
        Optional<IrrigationSettings> optionalSettings = irrigationSettingsRepository.findByCropIdAndUserId(cropId, userId);
        if (optionalSettings.isEmpty()) {
            return "Irrigation timing not set for this crop and user.";
        }
        IrrigationSettings settings = optionalSettings.get();
        return "Crop: " + settings.getCrop().getName() +
               "\nStart Time: " + settings.getStartTime() +
               "\nEnd Time: " + settings.getEndTime();
    }

    // Analyze sensor data and determine if irrigation should occur, based on the user’s settings.
    public String analyzeAndControlIrrigation(Long userId, Long cropId) {
        Optional<IrrigationSettings> optionalSettings = irrigationSettingsRepository.findByCropIdAndUserId(cropId, userId);
        if (optionalSettings.isEmpty()) {
            throw new RuntimeException("Irrigation settings not found for user and crop.");
        }
        IrrigationSettings settings = optionalSettings.get();
        LocalTime startTime = settings.getStartTime();
        LocalTime endTime = settings.getEndTime();

        if (startTime == null || endTime == null) {
            throw new RuntimeException("Irrigation timing not set for crop: " + settings.getCrop().getName());
        }

        StringBuilder result = new StringBuilder("Irrigation Analysis for Crop: " + settings.getCrop().getName() + "\n");

        // Check if current time falls within the irrigation window.
        if (!(LocalTime.now().isAfter(startTime) && LocalTime.now().isBefore(endTime))) {
            return "Not within the irrigation time window.";
        }

        // Fetch simulated real-time sensor data.
        double soilMoisture = Math.random() * 100;
        double temperature = Math.random() * 50;
        double humidity = Math.random() * 100;

        result.append("Current Conditions:\n")
              .append("Soil Moisture: ").append(soilMoisture).append(" (Min: ")
              .append(settings.getCrop().getMinSoilMoisture()).append(")\n")
              .append("Temperature: ").append(temperature).append(" (Min: ")
              .append(settings.getCrop().getMinTemperature()).append(", Max: ")
              .append(settings.getCrop().getMaxTemperature()).append(")\n")
              .append("Humidity: ").append(humidity).append(" (Min: ")
              .append(settings.getCrop().getMinHumidity()).append(", Max: ")
              .append(settings.getCrop().getMaxHumidity()).append(")\n");

        boolean valveShouldOpen = false;
        if (soilMoisture < settings.getCrop().getMinSoilMoisture()) {
            valveShouldOpen = true;
            result.append("Low Soil Moisture Detected. Irrigation Needed.\n");
        }
        if (temperature > settings.getCrop().getMaxTemperature()) {
            valveShouldOpen = true;
            result.append("High Temperature Detected. Irrigation Needed.\n");
        }
        if (temperature < settings.getCrop().getMinTemperature()) {
            valveShouldOpen = true;
            result.append("Low Temperature Detected. Irrigation Needed.\n");
        }
        if (humidity > settings.getCrop().getMaxHumidity()) {
            valveShouldOpen = true;
            result.append("High Humidity Detected. Irrigation Needed.\n");
        }
        if (humidity < settings.getCrop().getMinHumidity()) {
            valveShouldOpen = true;
            result.append("Low Humidity Detected. Irrigation Needed.\n");
        }

        if (!valveShouldOpen) {
            result.append("Conditions Optimal. No Irrigation Needed.\n");
        }

        // Update the manual control flag to reflect current valve status.
        settings.setIsManualControlEnabled(valveShouldOpen);
        irrigationSettingsRepository.save(settings);

        result.append("Irrigation Window Ended.");
        return result.toString();
    }

    // Allow manual control of the valve for a specific user and crop.
    public void manualValveControl(Long userId, Long cropId, boolean open, boolean close) {
        Optional<UserCrops> optionalSettings = userCropRepository.findByUserIdAndCropId(userId, cropId);
        if (optionalSettings.isEmpty()) {
            throw new RuntimeException("crop settings not found for this user and crop.");
        }
        UserCrops userCrop = optionalSettings.get();
        userCrop.setManualOpenEnabled(open);
        userCrop.setManualCloseEnabled(close);
        
        userCropRepository.save(userCrop);
        if (open) {
            String result = mosfetControlService.switchOnMosfet();
            // Optionally log or process result
            System.out.println("MOSFET switch on result: " + result);
        } else {
            String result = mosfetControlService.switchOffMosfet();
            // Optionally log or process result
            System.out.println("MOSFET switch off result: " + result);
        }
    }

    // Retrieve the current valve status for a specific user and crop.
    public String getCurrentStatus(Long userId, Long cropId) {
        Optional<IrrigationSettings> optionalSettings = irrigationSettingsRepository.findByCropIdAndUserId(cropId, userId);
        if (optionalSettings.isEmpty()) {
            return "No irrigation settings found for this user and crop.";
        }
        IrrigationSettings settings = optionalSettings.get();
        return settings.getIsManualControlEnabled() ? "Valve is OPEN" : "Valve is CLOSED";
    }
    
    @Scheduled(fixedRate = 10000) // Runs every 10 seconds
    public void scheduledAutomaticValveControl() {
        // Retrieve all user crops that require automated control
        List<UserCrops> userCropsList = userCropRepository.findAll();
        for (UserCrops userCrops : userCropsList) {
            Long userId = userCrops.getUser().getId();
            Long cropId = userCrops.getCrop().getId();
            automaticValveControl(userId, cropId);
        }
    }
    // automatic valve open code
    public String automaticValveControl(Long userId, Long cropId) {
        Optional<UserCrops> autoSettings = userCropRepository.findByUserIdAndCropId(userId, cropId);
        if (autoSettings.isPresent()) {
            UserCrops userCrops = autoSettings.get();
            if (userCrops.isManualOpenEnabled() || userCrops.isManualCloseEnabled()) {
                System.out.println("Manual control active for userId: " + userId + ", cropId: " + cropId + ". Skipping auto control.");
                return "Manual control active, skipping auto control";
            }
            LocalTime start = userCrops.getCustomIrrigationStartTime();
            LocalTime end = userCrops.getCustomIrrigationEndTime();
            LocalTime now = LocalTime.now();

            // Check if current time is within the irrigation window
            if (now.isAfter(start) && now.isBefore(end)) {
                // Retrieve latest sensor data, expecting three entries: Temperature, Humidity, SoilMoisture
                List<SensorData> sensorDataList = sensorDataRepository.findLatestSensorData(PageRequest.of(0, 4));
                if (sensorDataList.isEmpty()) {
                    System.out.println("No sensor data available.");
                } else {
                    Double temperature = null;
                    Double humidity = null;
                    Double soilMoisture = null;
                    for (SensorData data : sensorDataList) {
                        System.out.println("Sensor Type: " + data.getSensorType() + ", Value: " + data.getValue());
                        String sensorTypeNormalized = data.getSensorType().trim().toLowerCase();
                        if ("temperature".equals(sensorTypeNormalized)) {
                            temperature = data.getValue();
                        } else if ("humidity".equals(sensorTypeNormalized)) {
                            humidity = data.getValue();
                        } else if ("soilmoisture".equals(sensorTypeNormalized)) {
                            soilMoisture = data.getValue();
                        }
                    }
                    if (temperature == null || humidity == null || soilMoisture == null) {
                        System.out.println("Incomplete sensor data.");
                    } else {
                        boolean withinTempRange = temperature >= userCrops.getCustomMinTemperature() &&
                                temperature <= userCrops.getCustomMaxTemperature();
                        boolean withinHumidityRange = humidity >= userCrops.getCustomMinHumidity() &&
                                humidity <= userCrops.getCustomMaxHumidity();
                        boolean withinSoilMoistureRange = soilMoisture >= 65;

                        if (withinTempRange && withinHumidityRange && withinSoilMoistureRange) {
                            String result = mosfetControlService.switchOnMosfet();
                            System.out.println("Valve ON: " + result);
                        } else {
                            String result = mosfetControlService.switchOffMosfet();
                            System.out.println("Valve OFF: " + result);
                        }
                    }
                }
            } 
            
            // else {
            //     String result = mosfetControlService.switchOffMosfet();
            //     System.out.println("Outside irrigation window. Valve OFF: " + result);
            // }
            return "Control action executed";
        } else {
            return "No user found for this crop";
        }
    }
}
