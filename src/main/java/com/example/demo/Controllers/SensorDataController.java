package com.example.demo.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entities.SensorData;
import com.example.demo.Services.SensorDataService;

@RestController
@RequestMapping("/api/sensor")
public class SensorDataController {
    
    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService){
        this.sensorDataService = sensorDataService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<SensorData>> getAllSensorData(){
        List<SensorData> data = sensorDataService.getAllSensorData();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<SensorData>> getSensorDataByType(@PathVariable String type){
        List<SensorData> data = sensorDataService.getSensorDataByType(type);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/add")

    public ResponseEntity<String> addSensorData(@RequestBody SensorData sensorData){
        
        sensorDataService.saveSensorData(sensorData);
        return ResponseEntity.ok("Sensor Data added successfully");
    }
}
