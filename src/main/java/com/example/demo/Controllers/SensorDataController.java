package com.example.demo.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entities.SensorData;
import com.example.demo.Services.SensorDataService;

@RestController
@RequestMapping("api/sensor")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<SensorData>> getAllSensorData() {

        List<SensorData> data = sensorDataService.getAllSensorData();
        System.out.println("retrieved data : " + data);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<SensorData>> getSensorDataByType(@PathVariable String type){
        List<SensorData> sensorData = sensorDataService.getSensorDataByType(type);
        System.out.println(sensorData);
        if(sensorData.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sensorData);
    }

    @GetMapping("/notification")
    public ResponseEntity<List<String>> getNotifications(){
        List<String> notifications = new ArrayList<>();
        Double currentTemperature = sensorDataService.getLatestSensorData("temperature");
        Double currentSoilMoisture = sensorDataService.getLatestSensorData("soilMoisture");

        if(currentSoilMoisture > 80){
            notifications.add("High Soil Moisture Detected");
        }
        if(currentTemperature > 35.5){
            notifications.add("High Temperature Detected");
        }
        return ResponseEntity.ok(notifications);
    }
}
