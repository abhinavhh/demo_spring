package com.example.demo.Services;

import com.example.demo.Entities.SensorData;
import com.example.demo.Repositories.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorDataService {

    private final SensorDataRepository sensorDataRepository;
    private final UserService userService;

    public SensorDataService(SensorDataRepository sensorDataRepository, UserService userService) {
        this.sensorDataRepository = sensorDataRepository;
        this.userService = userService;
    }

    public void saveSensorData(SensorData sensorData) {
        sensorData.setUser(userService.getCurrentUser());
        sensorDataRepository.save(sensorData);
    }

    public List<SensorData> getSensorData(String sensorType) {
        Long userId = userService.getCurrentUser().getId();
        return sensorDataRepository.findByUserIdAndSensorTypeOrderByTimestampDesc(userId, sensorType);
    }
    public SensorData getLatestSensorData(String sensorType) {
        Long userId = userService.getCurrentUser().getId();
        List<SensorData> sensorDataList = sensorDataRepository.findByUserIdAndSensorTypeOrderByTimestampDesc(userId, sensorType);
        return sensorDataList.isEmpty() ? null : sensorDataList.get(0); // Return the latest data or null if none exists
    }

    public List<SensorData> getSensorDataByType(String sensorType) {
        Long userId = userService.getCurrentUser().getId();
        return sensorDataRepository.findByUserIdAndSensorTypeOrderByTimestampDesc(userId, sensorType);
    }

    
}
