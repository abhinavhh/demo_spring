package com.example.demo.Controllers;

import com.example.demo.Entities.Crops;
import com.example.demo.Repositories.CropRepository;
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
    public ResponseEntity<Iterable<Crops>> getAllCrops() {
        return ResponseEntity.ok(cropRepository.findAll());
    }
}