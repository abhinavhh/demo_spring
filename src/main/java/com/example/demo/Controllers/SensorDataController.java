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
import java.time.temporal.WeekFields;
import java.util.Locale;

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
    
    @GetMapping("/latest")
    public ResponseEntity<SensorData> getLatestSensorData() {
        SensorData latestData = sensorDataService.getLatestData(); 
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
        
        // Return empty list if no data is found
        if (rawData == null || rawData.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Map<String, Object>> aggregatedData;

        switch (filter.toLowerCase()) {
            case "day":
                // Get all data points for the current day with timestamps
                aggregatedData = aggregateDataForDay(rawData);
                break;

            case "week":
                // Get the average data for each day of the last 7 days
                aggregatedData = aggregateDataForWeek(rawData);
                break;

            case "month":
                // Get the average data for each week of the last 4 weeks
                aggregatedData = aggregateDataForMonth(rawData);
                break;

            default:
                return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        return ResponseEntity.ok(aggregatedData);
    }

    private List<Map<String, Object>> aggregateDataForDay(List<SensorData> rawData) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);

        // Filter data for the last 24 hours and sort by timestamp
        return rawData.stream()
                .filter(data -> data.getTimestamp().isAfter(twentyFourHoursAgo))
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .map(data -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("timestamp", data.getTimestamp());
                    result.put("value", data.getValue());
                    result.put("date", data.getTimestamp().toLocalDate().toString()); // Add date string for display
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
            LocalDate date = entry.getKey();
            dailyData.put("date", date.toString());
            dailyData.put("value", avgValue); // Consistent key name
            dailyData.put("timestamp", date.atStartOfDay()); // Use start of day for timestamp
            result.add(dailyData);
        }
        
        // Sort by date
        result.sort((a, b) -> ((String)a.get("date")).compareTo((String)b.get("date")));
        return result;
    }
    
    private List<Map<String, Object>> aggregateDataForMonth(List<SensorData> rawData) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fourWeeksAgo = now.minusWeeks(4);
        
        // Filter data for the last 4 weeks
        List<SensorData> filteredData = rawData.stream()
                .filter(data -> data.getTimestamp().isAfter(fourWeeksAgo))
                .collect(Collectors.toList());
        
        // Group data by week
        Map<Integer, List<SensorData>> groupedByWeek = new HashMap<>();
        
        // Using WeekFields to get week of year
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        
        filteredData.forEach(sensorData -> {
            LocalDate date = sensorData.getTimestamp().toLocalDate();
            int weekOfYear = date.get(weekFields.weekOfWeekBasedYear());
            int year = date.get(weekFields.weekBasedYear());
            // Unique key for week: year * 100 + weekOfYear
            int weekKey = year * 100 + weekOfYear;
            
            groupedByWeek.computeIfAbsent(weekKey, _-> new ArrayList<>()).add(sensorData);
        });
    
        // Calculate the average for each week
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        for (Map.Entry<Integer, List<SensorData>> entry : groupedByWeek.entrySet()) {
            List<SensorData> weekData = entry.getValue();
            double avg = weekData.stream().mapToDouble(SensorData::getValue).average().orElse(0.0);
            
            // Find the first day of this week for date representation
            LocalDate weekStartDate = weekData.stream()
                .map(data -> data.getTimestamp().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
            
            Map<String, Object> aggregated = new HashMap<>();
            aggregated.put("value", avg); // Consistent key name
            aggregated.put("date", "Week of " + weekStartDate.toString());
            aggregated.put("timestamp", weekStartDate.atStartOfDay()); // Use start of week for timestamp
            aggregated.put("weekNumber", entry.getKey() % 100); // Extract week number for display
            
            monthlyData.add(aggregated);
        }
        
        // Sort by week start date
        monthlyData.sort((a, b) -> ((LocalDateTime)a.get("timestamp")).compareTo((LocalDateTime)b.get("timestamp")));
        return monthlyData;
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