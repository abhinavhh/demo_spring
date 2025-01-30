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
import java.util.Random;  // Added this import
import java.util.stream.Collectors;

@Service
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;
    private final Random random = new Random();  // Added this field

    public SensorDataService(SensorDataRepository sensorDataRepository, UserService userService) {
        this.sensorDataRepository = sensorDataRepository;
    }

    public SensorData saveSensorData(SensorData sensorData) {
        sensorData.setTimestamp(LocalDateTime.now());
        return sensorDataRepository.save(sensorData);
    }

    public List<SensorData> getSensorData(String sensorType) {
        return sensorDataRepository.findAll();
    }

    // Method to fetch the latest sensor data
    public SensorData getLatestData() {
        Optional<SensorData> latestData = sensorDataRepository.findTopByOrderByIdDesc();
        return latestData.orElseThrow(() -> new RuntimeException("No sensor data found"));
    }

    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }

    public List<SensorData> getSensorDataByType(String sensorType){
        return sensorDataRepository.findBySensorType(sensorType);
    }

    public List<SensorData> getFilteredSensorData(String sensorType, String filter) {
        LocalDateTime startTime;
        switch(filter.toLowerCase()){
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

    public Flux<SensorData> getAllSensorDataStream() {
        return Flux.fromIterable(sensorDataRepository.findAll())
                   .repeat()
                   .delayElements(Duration.ofSeconds(2));
    }

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
            data.setValue(random.nextDouble() * 100);  // Now using the random field
            data.setTimestamp(LocalDateTime.now());
            sensorDataRepository.save(data);
        }
    }
}