package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.SensorData;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long>{
    List<SensorData> findAll();
    List<SensorData> findBySensorType(String type);
    Double getSensorValue();
}
