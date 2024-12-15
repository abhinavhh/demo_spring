package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entities.SensorData;

public interface SensorDataRepository extends JpaRepository<SensorData, Long>{

    List<SensorData> findBySensorType(String sensorType);
    
}
