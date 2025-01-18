package com.example.demo.Controllers;

import com.example.demo.Entities.Crops;
import com.example.demo.Repositories.CropRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/crops")
public class CropController {
    private final CropRepository cropRepository;

    public CropController(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCrop(@RequestBody Crops crop) {
        cropRepository.save(crop);
        return ResponseEntity.ok("Crop added successfully!");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllCrops() {
        List<Map<String, Object>> cropsWithThresholds = cropRepository.findAll()
            .stream()
            .map(crop -> {
                Map<String, Object> cropMap = new HashMap<>();
                cropMap.put("id", crop.getId());
                cropMap.put("name", crop.getName());
                cropMap.put("minTemperature", crop.getMinTemperature());
                cropMap.put("maxTemperature", crop.getMaxTemperature());
                cropMap.put("minHumidity", crop.getMinHumidity());
                cropMap.put("maxHumidity", crop.getMaxHumidity());
                cropMap.put("minSoilMoisture", crop.getMinSoilMoisture());
                cropMap.put("maxSoilMoisture", crop.getMaxSoilMoisture());
                return cropMap;
            })
            .toList();
        return ResponseEntity.ok(cropsWithThresholds);
    }


}