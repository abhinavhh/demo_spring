package com.example.demo.Services;

import com.example.demo.Entities.SensorData;
import com.example.demo.Repositories.SensorDataRepository;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;
    private final Random random = new Random();

    public SensorDataService(SensorDataRepository sensorDataRepository, UserService userService) {
        this.sensorDataRepository = sensorDataRepository;
    }

    // Save sensor data with the current timestamp.
    // Ensure that the provided SensorData object has a valid Users reference.
    public SensorData saveSensorData(SensorData sensorData) {
        sensorData.setTimestamp(LocalDateTime.now());
        return sensorDataRepository.save(sensorData);
    }

    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }

    // Return sensor data only for a specific user.
    public List<SensorData> getSensorDataByUser(Long userId) {
        return sensorDataRepository.findByUserId(userId);
    }

    // Return sensor data by sensor type for a specific user.
    public List<SensorData> getSensorDataByTypeAndUser(String sensorType, Long userId) {
        return sensorDataRepository.findBySensorTypeAndUserId(sensorType, userId);
    }

    // Fetch the latest sensor data (global) 
    public SensorData getLatestData() {
        Optional<SensorData> latestData = sensorDataRepository.findTopByOrderByIdDesc();
        return latestData.orElseThrow(() -> new RuntimeException("No sensor data found"));
    }

    // Fetch sensor data by type (global)
    public List<SensorData> getSensorDataByType(String sensorType) {
        return sensorDataRepository.findBySensorType(sensorType);
    }

    // Return filtered sensor data by sensor type for a specific user.
    public List<SensorData> getFilteredSensorDataByUser(String sensorType, String filter, Long userId) {
        LocalDateTime startTime;
        switch (filter.toLowerCase()) {
            case "week":
                startTime = LocalDateTime.now().minusWeeks(1);
                break;
            case "month":
                startTime = LocalDateTime.now().minusWeeks(1); // Adjust if needed
                break;
            case "day":
            default:
                startTime = LocalDateTime.now().minusDays(1);
        }
        // Ensure your repository has this method.
        return sensorDataRepository.findBySensorTypeAndUserIdAndTimestampAfter(sensorType, userId, startTime);
    }

    // Global filtering method (if needed)
    public List<SensorData> getFilteredSensorData(String sensorType, String filter) {
        LocalDateTime startTime;
        switch (filter.toLowerCase()) {
            case "week":
                startTime = LocalDateTime.now().minusWeeks(1);
                break;
            case "month":
                startTime = LocalDateTime.now().minusWeeks(1);
                break;
            case "day":
            default:
                startTime = LocalDateTime.now().minusDays(1);
        }
        return sensorDataRepository.findBySensorTypeAndTimestampAfter(sensorType, startTime);
    }

    // Return a Flux stream of all sensor data (global)
    public Flux<SensorData> getAllSensorDataStream() {
        return Flux.fromIterable(sensorDataRepository.findAll())
                   .repeat()
                   .delayElements(Duration.ofSeconds(2));
    }

    // Return aggregated multi-sensor data for a specific user.
    public List<Map<String, Object>> getMultiSensorDataForUser(Long userId) {
        List<SensorData> userData = sensorDataRepository.findByUserId(userId);
        return userData.stream().map(data -> {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("timestamp", data.getTimestamp());
            dataPoint.put(data.getSensorType(), data.getValue());
            return dataPoint;
        }).collect(Collectors.toList());
    }

    // Return aggregated multi-sensor data (global)
    public List<Map<String, Object>> getMultiSensorData() {
        List<SensorData> allData = sensorDataRepository.findAll();
        return allData.stream().map(data -> {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("timestamp", data.getTimestamp());
            dataPoint.put(data.getSensorType(), data.getValue());
            return dataPoint;
        }).collect(Collectors.toList());
    }

    public void generateRandomSensorData() {
        String[] sensorTypes = {"Temperature", "Humidity", "Soil Moisture"};
        for (String sensorType : sensorTypes) {
            SensorData data = new SensorData();
            data.setSensorType(sensorType);
            data.setValue(random.nextDouble() * 100);
            data.setTimestamp(LocalDateTime.now());
            sensorDataRepository.save(data);
        }
    }

    public SensorData getLatestDataByUser(Long userId) {
        Optional<SensorData> latestData = sensorDataRepository.findTopByUserIdOrderByIdDesc(userId);
        return latestData.orElseThrow(() -> new RuntimeException("No sensor data found for user " + userId));
    }
    
}
