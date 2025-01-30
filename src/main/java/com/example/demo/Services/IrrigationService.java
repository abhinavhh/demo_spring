package com.example.demo.Services;

import com.example.demo.Entities.Crops;
import com.example.demo.Repositories.CropRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class IrrigationService {

    private final CropRepository cropRepository;
    private boolean valveOpen = false;

    public IrrigationService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    public void setIrrigationTiming(Long cropId, String startTime, String endTime) {
        Optional<Crops> optionalCrop = cropRepository.findById(cropId);
        if (optionalCrop.isEmpty()) {
            throw new RuntimeException("Crop not found!");
        }
    
        Crops crop = optionalCrop.get();
        crop.setIrrigationStartTime(LocalTime.parse(startTime));
        crop.setIrrigationEndTime(LocalTime.parse(endTime));
        cropRepository.save(crop);
    }
    public String analyzeAndControlIrrigation(Long cropId) {
        Optional<Crops> optionalCrop = cropRepository.findById(cropId);
        if (optionalCrop.isEmpty()) throw new RuntimeException("Crop not found!");
    
        Crops crop = optionalCrop.get();
    
        // Retrieve irrigation start and end times from the crop
        LocalTime startTime = crop.getIrrigationStartTime();
        LocalTime endTime = crop.getIrrigationEndTime();
    
        if (startTime == null || endTime == null) {
            throw new RuntimeException("Irrigation timing not set for crop: " + crop.getName());
        }
    
        StringBuilder result = new StringBuilder("Irrigation Analysis for Crop: " + crop.getName() + "\n");
    
        if (!(LocalTime.now().isAfter(startTime) && LocalTime.now().isBefore(endTime))) {
            return "Not within the irrigation time window.";
        }
    
        boolean valveOpen = false;
    
        // Fetch simulated real-time sensor data
        double soilMoisture = Math.random() * 100;
        double temperature = Math.random() * 50;
        double humidity = Math.random() * 100;
    
        result.append("Current Conditions:\n")
              .append("Soil Moisture: ").append(soilMoisture).append(" (Min: ").append(crop.getMinSoilMoisture()).append(")\n")
              .append("Temperature: ").append(temperature).append(" (Min: ").append(crop.getMinTemperature()).append(", Max: ").append(crop.getMaxTemperature()).append(")\n")
              .append("Humidity: ").append(humidity).append(" (Min: ").append(crop.getMinHumidity()).append(", Max: ").append(crop.getMaxHumidity()).append(")\n");
    
        // Analyze conditions
        if (soilMoisture < crop.getMinSoilMoisture()) {
            valveOpen = true;
            result.append("Low Soil Moisture Detected. Irrigation Needed.\n");
        }
        if (temperature > crop.getMaxTemperature()) {
            valveOpen = true;
            result.append("High Temperature Detected. Irrigation Needed.\n");
        }
        if (temperature < crop.getMinTemperature()) {
            valveOpen = true;
            result.append("Low Temperature Detected. Irrigation Needed.\n");
        }
        if (humidity > crop.getMaxHumidity()) {
            valveOpen = true;
            result.append("High Humidity Detected. Irrigation Needed.\n");
        }
        if (humidity < crop.getMinHumidity()) {
            valveOpen = true;
            result.append("Low Humidity Detected. Irrigation Needed.\n");
        }
    
        if (!valveOpen) {
            result.append("Conditions Optimal. No Irrigation Needed.\n");
        }
    
        result.append("Irrigation Window Ended.");
    
        return result.toString();
    }
    public String getIrrigationTiming(Long cropId) {
        Optional<Crops> optionalCrop = cropRepository.findById(cropId);
        if (optionalCrop.isEmpty()) {
            throw new RuntimeException("Crop not found!");
        }
    
        Crops crop = optionalCrop.get();
        LocalTime startTime = crop.getIrrigationStartTime();
        LocalTime endTime = crop.getIrrigationEndTime();
    
        if (startTime == null || endTime == null) {
            return "Irrigation timing not set for crop: " + crop.getName();
        }
    
        return "Crop: " + crop.getName() +
               "\nStart Time: " + startTime +
               "\nEnd Time: " + endTime;
    }
    

    public void manualValveControl(boolean openValve) {
        valveOpen = openValve;
    }

    public String getCurrentStatus() {
        return valveOpen ? "Valve is OPEN" : "Valve is CLOSED";
    }
}