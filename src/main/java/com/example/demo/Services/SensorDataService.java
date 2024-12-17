package com.example.demo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.Entities.SensorData;
import com.example.demo.Repositories.SensorDataRepository;

@Service
public class SensorDataService {
   private final SensorDataRepository sensorDataRepository;
     public SensorDataService(SensorDataRepository sensorDataRepository){
        this.sensorDataRepository = sensorDataRepository;
     }
     public List<SensorData> getAllSensorData(){
        return sensorDataRepository.findAll();
     }
     public List<SensorData> getSensorDataByType(String type){
        return sensorDataRepository.findBySensorType(type);
     }
}
