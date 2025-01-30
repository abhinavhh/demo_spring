package com.example.demo.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entities.SensorData;
import com.example.demo.Services.SensorDataService;

import java.util.Collections;


import java.time.LocalDate;
import java.time.LocalDateTime;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/sensor")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        List<SensorData> data = sensorDataService.getAllSensorData();
        return ResponseEntity.ok(data);
    }
    @GetMapping("/sensor/latest")
    public ResponseEntity<SensorData> getLatestSensorData() {
        SensorData latestData = sensorDataService.getLatestData(); // Fetch the latest data from your DB
        return ResponseEntity.ok(latestData);
    }
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SensorData>> getSensorDataByType(@PathVariable String type) {
        List<SensorData> data = sensorDataService.getSensorDataByType(type);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<Map<String, Object>>> getFilteredSensorData(
        @PathVariable String type, @RequestParam(defaultValue = "day") String filter) {

        List<SensorData> rawData = sensorDataService.getSensorDataByType(type);

        List<Map<String, Object>> aggregatedData = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "day":
                // Get real-time data for the last 24 hours
                aggregatedData = aggregateDataFor24Hours(rawData);
                break;

            case "week":
                // Get the average data for the last 7 days
                aggregatedData = aggregateDataForWeek(rawData);
                break;

            case "month":
                // Get the average data for every 3 days over the last 30 days
                aggregatedData = aggregateDataForMonth(rawData);
                break;

            default:
                return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        return ResponseEntity.ok(aggregatedData);
    }

    private List<Map<String, Object>> aggregateDataFor24Hours(List<SensorData> rawData) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);

        // Filter data for the last 24 hours
        return rawData.stream()
                .filter(data -> data.getTimestamp().isAfter(twentyFourHoursAgo))
                .map(data -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("timestamp", data.getTimestamp());
                    result.put("value", data.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }
    private List<Map<String, Object>> aggregateDataForWeek(List<SensorData> rawData) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
    
        // Filter data for the last 7 days
        Map<LocalDate, List<SensorData>> groupedByDate = rawData.stream()
                .filter(data -> data.getTimestamp().isAfter(sevenDaysAgo))
                .collect(Collectors.groupingBy(data -> data.getTimestamp().toLocalDate()));
    
        // Calculate average value for each day
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<SensorData>> entry : groupedByDate.entrySet()) {
            double avgValue = entry.getValue().stream()
                    .mapToDouble(SensorData::getValue)
                    .average()
                    .orElse(0.0);
    
            Map<String, Object> dailyData = new HashMap<>();
            dailyData.put("date", entry.getKey());
            dailyData.put("averageValue", avgValue);
            result.add(dailyData);
        }
        return result;
    }
    
    private List<Map<String, Object>> aggregateDataForMonth(List<SensorData> rawData) {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        
        // Group data by 3-day periods
        Map<String, List<SensorData>> groupedByPeriod = new HashMap<>();
        
        rawData.forEach(sensorData -> {
            String period = getMonthPeriod(sensorData.getTimestamp());
            groupedByPeriod.computeIfAbsent(period, _ -> new ArrayList<>()).add(sensorData);
        });
    
        // Calculate the average for each period (e.g., 3-day range)
        for (Map.Entry<String, List<SensorData>> entry : groupedByPeriod.entrySet()) {
            List<SensorData> periodData = entry.getValue();
            double avg = periodData.stream().mapToDouble(SensorData::getValue).average().orElse(0.0);
            
            Map<String, Object> aggregated = new HashMap<>();
            aggregated.put("averageValue", avg);
            aggregated.put("startPeriod", periodData.get(0).getTimestamp());
            aggregated.put("endPeriod", periodData.get(periodData.size() - 1).getTimestamp());
            
            monthlyData.add(aggregated);
        }
        
        return monthlyData;
    }

    // Helper method to extract a 3-day period based on the timestamp
    private String getMonthPeriod(LocalDateTime timestamp) {
    // Group by 3-day intervals
        int dayOfMonth = timestamp.getDayOfMonth();
        int startPeriod = (dayOfMonth / 3) * 3 + 1; // Ensure starting from 1st day of the month in 3-day chunks
        return String.format("%04d-%02d-%02d", timestamp.getYear(), timestamp.getMonthValue(), startPeriod);
    }
    

    @PostMapping("/add")
    public ResponseEntity<String> addSensorData(@RequestBody SensorData sensorData) {
        sensorDataService.saveSensorData(sensorData);
        return ResponseEntity.ok("Sensor Data added successfully");
    }

    @GetMapping("/multi")
    public ResponseEntity<List<Map<String, Object>>> getMultiSensorData() {
        List<Map<String, Object>> multiSensorData = sensorDataService.getMultiSensorData();
        return ResponseEntity.ok(multiSensorData);
    }
}
