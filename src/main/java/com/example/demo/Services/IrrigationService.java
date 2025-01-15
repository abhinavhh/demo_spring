package com.example.demo.Services;

import com.example.demo.Entities.Crops;
import com.example.demo.Repositories.CropRepository;
import org.springframework.stereotype.Service;
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
        // ✅ Simulating Real-Time Sensor Data (Adjust with real readings)
        double soilMoisture = Math.random() * 100;
        double temperature = Math.random() * 50;
        double humidity = Math.random() * 100;

        // ✅ Analyze Conditions
        if (soilMoisture < crop.getMinSoilMoisture() || 
            temperature > crop.getMaxTemperature() || 
            humidity < crop.getMinHumidity()) {
            valveOpen = true;
            System.out.println("Automatic Irrigation Triggered for Crop: " + crop.getName());
        } else {
            valveOpen = false;
            System.out.println("No Irrigation Needed.");
        }
    }

    public void manualValveControl(boolean openValve) {
        valveOpen = openValve;
    }

    public String getCurrentStatus() {
        return valveOpen ? "Valve is OPEN" : "Valve is CLOSED";
    }
}
