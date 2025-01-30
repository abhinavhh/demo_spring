package com.example.demo.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.SensorData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findBySensorTypeAndTimestampAfter(String sensorType, LocalDateTime timestamp);
    List<SensorData> findBySensorType(String sensorType);
    List<SensorData> findBySensorTypeOrderByTimestampDesc(String sensorType);
    Optional<SensorData> findTopByOrderByIdDesc();
}
