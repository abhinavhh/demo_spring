package com.example.demo.Services;

import com.example.demo.Entities.SensorData;
import com.example.demo.Repositories.SensorDataRepository;


import reactor.core.publisher.Flux;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {

    private final SensorDataRepository sensorDataRepository;

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
    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }

    public List<SensorData> getSensorDataByType(String sensorType) {
        return sensorDataRepository.findBySensorType(sensorType);
    }

    public Flux<SensorData> getAllSensorDataStream() {
        return Flux.fromIterable(sensorDataRepository.findAll()).delayElements(Duration.ofSeconds(2));
    }

    
}
