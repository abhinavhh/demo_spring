package com.example.demo.Repositories;

import com.example.demo.Entities.SensorData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    
    // Find all sensor data for a given sensor type.
    List<SensorData> findBySensorType(String sensorType);
    
    // Find all sensor data for a specific user.
    List<SensorData> findByUserId(Long userId);

    List<SensorData> findBySensorTypeAndUserId(String sensorType, Long userId);
    
    // Find sensor data of a given type recorded after a specific timestamp.
    List<SensorData> findBySensorTypeAndTimestampAfter(String sensorType, LocalDateTime timestamp);
    
    List<SensorData> findBySensorTypeAndUserIdAndTimestampAfter(String sensorType, Long userId, LocalDateTime timestamp);
    // Retrieve the most recent sensor data entry.
    Optional<SensorData> findTopByOrderByIdDesc();

    Optional<SensorData> findTopByUserIdOrderByIdDesc(Long userId);

}
