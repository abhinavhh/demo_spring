package com.example.demo.Controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entities.SensorData;
import com.example.demo.Services.SensorDataService;
@CrossOrigin(origins = "https://smart-irrigation-rho.vercel.app")
@RestController
@RequestMapping("/api/sensor")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    // Optionally filter sensor data by userId (if not provided, return all sensor data)
    @GetMapping("/all")
    public ResponseEntity<List<SensorData>> getAllSensorData(@RequestParam(required = false) Long userId) {
        List<SensorData> data;
        if (userId != null) {
            data = sensorDataService.getSensorDataByUser(userId);
        } else {
            data = sensorDataService.getAllSensorData();
        }
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestSensorData(@RequestParam(required = false) Long userId) {
        try {
            SensorData latestData;
            if (userId != null) {
                latestData = sensorDataService.getLatestDataByUser(userId);
            } else {
                latestData = sensorDataService.getLatestData();
            }
            
            if (latestData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("No sensor data found.");
            }
            return ResponseEntity.ok(latestData);
        } catch (Exception e) {
            // Log the exception details for debugging
            System.err.println("Error fetching sensor data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error fetching sensor data.");
        }
    }


    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SensorData>> getSensorDataByType(
        @PathVariable String type,
        @RequestParam(required = false) Long userId) {
        List<SensorData> data;
        if (userId != null) {
            // Call a service method that filters by sensor type and user
            data = sensorDataService.getSensorDataByTypeAndUser(type, userId);
        } else {
            data = sensorDataService.getSensorDataByType(type);
        }
        return ResponseEntity.ok(data);
    }
    

    // The multi-sensor aggregation endpoint remains unchanged.
    @GetMapping("/{type}")
    public ResponseEntity<List<Map<String, Object>>> getFilteredSensorData(
        @PathVariable String type, @RequestParam(defaultValue = "day") String filter) {

        List<SensorData> rawData = sensorDataService.getSensorDataByType(type);
        
        if (rawData == null || rawData.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Map<String, Object>> aggregatedData;
        switch (filter.toLowerCase()) {
            case "day":
                aggregatedData = aggregateDataForDay(rawData);
                break;
            case "week":
                aggregatedData = aggregateDataForWeek(rawData);
                break;
            case "month":
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

        return rawData.stream()
                .filter(data -> data.getTimestamp().isAfter(twentyFourHoursAgo))
                .sorted(Comparator.comparing(SensorData::getTimestamp))
                .map(data -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("timestamp", data.getTimestamp());
                    result.put("value", data.getValue());
                    result.put("date", data.getTimestamp().toLocalDate().toString());
                    return result;
                })
                .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> aggregateDataForWeek(List<SensorData> rawData) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
    
        Map<LocalDate, List<SensorData>> groupedByDate = rawData.stream()
                .filter(data -> data.getTimestamp().isAfter(sevenDaysAgo))
                .collect(Collectors.groupingBy(data -> data.getTimestamp().toLocalDate()));
    
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<SensorData>> entry : groupedByDate.entrySet()) {
            double avgValue = entry.getValue().stream()
                    .mapToDouble(SensorData::getValue)
                    .average()
                    .orElse(0.0);
    
            Map<String, Object> dailyData = new HashMap<>();
            LocalDate date = entry.getKey();
            dailyData.put("date", date.toString());
            dailyData.put("value", avgValue);
            dailyData.put("timestamp", date.atStartOfDay());
            result.add(dailyData);
        }
        result.sort(Comparator.comparing(m -> (String) m.get("date")));
        return result;
    }
    
    private List<Map<String, Object>> aggregateDataForMonth(List<SensorData> rawData) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fourWeeksAgo = now.minusWeeks(4);
        
        List<SensorData> filteredData = rawData.stream()
                .filter(data -> data.getTimestamp().isAfter(fourWeeksAgo))
                .collect(Collectors.toList());
        
        Map<Integer, List<SensorData>> groupedByWeek = new HashMap<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        
        filteredData.forEach(sensorData -> {
            LocalDate date = sensorData.getTimestamp().toLocalDate();
            int weekOfYear = date.get(weekFields.weekOfWeekBasedYear());
            int year = date.get(weekFields.weekBasedYear());
            int weekKey = year * 100 + weekOfYear;
            groupedByWeek.computeIfAbsent(weekKey, k -> new ArrayList<>()).add(sensorData);
        });
    
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        for (Map.Entry<Integer, List<SensorData>> entry : groupedByWeek.entrySet()) {
            List<SensorData> weekData = entry.getValue();
            double avg = weekData.stream().mapToDouble(SensorData::getValue).average().orElse(0.0);
            
            LocalDate weekStartDate = weekData.stream()
                .map(data -> data.getTimestamp().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
            
            Map<String, Object> aggregated = new HashMap<>();
            aggregated.put("value", avg);
            aggregated.put("date", "Week of " + weekStartDate.toString());
            aggregated.put("timestamp", weekStartDate.atStartOfDay());
            aggregated.put("weekNumber", entry.getKey() % 100);
            
            monthlyData.add(aggregated);
        }
        
        monthlyData.sort(Comparator.comparing(m -> (LocalDateTime) m.get("timestamp")));
        return monthlyData;
    }

    // When adding sensor data, expect the request body to include user information.
    // Alternatively, you could pass a userId parameter and look up the User entity.
    @PostMapping("/add")
    public ResponseEntity<String> addSensorData(@RequestBody SensorData sensorData) {
        sensorDataService.saveSensorData(sensorData);
        return ResponseEntity.ok("Sensor Data added successfully");
    }

    // Endpoint to return aggregated multi-sensor data
    @GetMapping("/multi")
    public ResponseEntity<List<Map<String, Object>>> getMultiSensorData(
        @RequestParam(required = false) Long userId) {
        List<Map<String, Object>> multiSensorData;
        if (userId != null) {
            multiSensorData = sensorDataService.getMultiSensorDataForUser(userId);
        } else {
            multiSensorData = sensorDataService.getMultiSensorData();
        }
        return ResponseEntity.ok(multiSensorData);
    }
    
}
