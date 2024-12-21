package com.example.demo.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entities.Crop;
import com.example.demo.Services.CropService;
@Controller
@RestController
@RequestMapping("/api/crops")
public class CropController {
    
    private final CropService cropService;

    public CropController(CropService cropService){
        this.cropService = cropService;
    }
    @GetMapping
    public ResponseEntity<List<Crop>> getAllCrops(){
        return ResponseEntity.ok(cropService.getAllCrops());
    }

    @PostMapping
    public ResponseEntity<Crop> addCrop(@RequestBody Crop crop){
        return ResponseEntity.ok(cropService.addCrop(crop));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCrop(@PathVariable Long id){

        cropService.deleteCrop(id);
        return ResponseEntity.ok("Crop Deleted");
    }
}
