package com.example.demo.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Services.IrrigationService;

@RestController
@RequestMapping("/api/irrigation")
public class IrrigationController {
    
    private final IrrigationService irrigationService;
    public IrrigationController(IrrigationService irrigationService){
        this.irrigationService = irrigationService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startIrrigation(){
        irrigationService.startIrrigation();
        return ResponseEntity.ok("Irrigation Started");
    }
    @PostMapping("/stop")
    public ResponseEntity<String> stopIrrigation(){
        irrigationService.stopIrrigation();
        return ResponseEntity.ok("Irrigation Stoped");
    }
}
