package com.example.demo.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.CropAnalysisResult;
import com.example.demo.Services.CropAnalysisService;

@Controller
@RestController
@RequestMapping("/api/analysis")
public class CropAnalysisController {
    
    private CropAnalysisService cropAnalysisService;

    public CropAnalysisController(CropAnalysisService cropAnalysisService){
        this.cropAnalysisService = cropAnalysisService;
    }

    @GetMapping
    public ResponseEntity<List<CropAnalysisResult>> getCropAnalysis(){
        
        return ResponseEntity.ok(cropAnalysisService.analyzeCropData());
    }
}
