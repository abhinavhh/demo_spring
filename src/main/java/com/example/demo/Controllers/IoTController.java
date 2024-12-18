package com.example.demo.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Entities.SensorData;
import com.example.demo.Services.IoTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/iot")
public class IoTController {
    

    private final IoTService iotService;
    public IoTController(IoTService iotService){
        this.iotService = iotService;
    }

    @PostMapping("/data")
    public ResponseEntity<String> receiveSensorData(@RequestBody SensorData sensorData) {
        iotService.saveSensorData(sensorData);
        return ResponseEntity.ok("Data Received");
    }
    
}
