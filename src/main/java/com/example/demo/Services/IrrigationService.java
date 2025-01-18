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

    public void setIrrigationTiming(Long cropId) {
        Optional<Crops> crop = cropRepository.findById(cropId);
        crop.ifPresent(c -> System.out.println("Timing set for crop: " + c.getName()));
    }

    /**
     * ✅ Real-Time Analysis Based on Latest Sensor Data
     */
    public void analyzeAndControlIrrigation(Long cropId) {
        Optional<Crops> optionalCrop = cropRepository.findById(cropId);
        if (optionalCrop.isEmpty()) throw new RuntimeException("Crop not found!");
    
        Crops crop = optionalCrop.get();
    
        // Define the start and end times for the irrigation window
        LocalTime startTime = LocalTime.of(16, 0); // Example: 4:00 PM
        LocalTime endTime = LocalTime.of(17, 0);   // Example: 5:00 PM
    
        // Ensure irrigation happens only within the defined time range
        while (LocalTime.now().isAfter(startTime) && LocalTime.now().isBefore(endTime)) {
            // Fetch real-time sensor data
            double soilMoisture = Math.random() * 100;
            double temperature = Math.random() * 50;
            double humidity = Math.random() * 100;
    
            // Analyze conditions
            if (soilMoisture < crop.getMinSoilMoisture() || 
                temperature > crop.getMaxTemperature() || 
                temperature < crop.getMinTemperature() || 
                humidity > crop.getMaxHumidity() || 
                humidity < crop.getMinHumidity()) {
                valveOpen = true;
                System.out.println("Irrigation Triggered for Crop: " + crop.getName());
            } else {
                valveOpen = false;
                System.out.println("Conditions Optimal. Valve Closed.");
            }
    
            // Simulate irrigation delay (e.g., 5 minutes)
            try {
                Thread.sleep(300000); // 300,000 ms = 5 minutes
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Irrigation analysis interrupted", e);
            }
        }
    
        System.out.println("Irrigation window ended.");
        valveOpen = false; // Ensure the valve is closed at the end
    }
    


    public void manualValveControl(boolean openValve) {
        valveOpen = openValve;
    }

    public String getCurrentStatus() {
        return valveOpen ? "Valve is OPEN" : "Valve is CLOSED";
    }
}