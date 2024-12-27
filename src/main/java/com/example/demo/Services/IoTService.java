package com.example.demo.Services;

import org.springframework.stereotype.Service;

import com.example.demo.Entities.SensorData;
import com.example.demo.Repositories.SensorDataRepository;

@Service
public class IoTService {
    private final SensorDataRepository sensorDataRepository;
    public IoTService(SensorDataRepository sensorDataRepository){
        this.sensorDataRepository = sensorDataRepository;
    }
    public void saveSensorData(SensorData sensorData){
        sensorDataRepository.save(sensorData);
    }
}