package com.example.demo.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.DTO.CropAnalysisResult;
import com.example.demo.Entities.Crop;
import com.example.demo.Repositories.CropRepository;

@Service
public class CropAnalysisService {
    
    private final CropRepository cropRepository;
    private final SensorDataService sensorDataService;

    public CropAnalysisService(CropRepository cropRepository, SensorDataService sensorDataService){
        this.cropRepository = cropRepository;
        this.sensorDataService = sensorDataService;
    }

    public List<CropAnalysisResult> analyzeCropData(){
        List<Crop> crops = cropRepository.findAll();
        List<CropAnalysisResult> results = new ArrayList<>();

        for(Crop crop : crops){

            Double temperature = sensorDataService.getLatestSensorData("temperature");
            Double humidity = sensorDataService.getLatestSensorData("humidity");
            Double soilMoisture = sensorDataService.getLatestSensorData("soilMoisture");

            results.add(new CropAnalysisResult(
                crop.getname(),
                "temperature",
                temperature,
                compareWithLimits(temperature,crop.getMinTemperature(),crop.getMaxTemperature())
            ));

            results.add(new CropAnalysisResult(
                crop.getname(),
                "humidity",
                humidity,
                compareWithLimits(humidity,crop.getMinHumidity(),crop.getMaxHumidity())
            ));

            results.add(new CropAnalysisResult(
                crop.getname(),
                "soilMoisture",
                soilMoisture,
                compareWithLimits(soilMoisture,crop.getMinSoilMoisture(),crop.getMaxSoilMoisture())
            ));
        }
        return results;
    }

    private String compareWithLimits(Double value,Double min,Double max){

        if(value < min) return "low";
        if(value > max) return "high";
        return "Within Limits";
    }
}
