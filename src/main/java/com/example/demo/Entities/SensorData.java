package com.example.demo.Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensor_data")
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_type")

    private String sensorType; // Remove `@SuppressWarnings`

    @Column(name = "value")
    private BigDecimal value; // Remove `@SuppressWarnings`

    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;


    public String getSensorType() {
        return sensorType;
    }

    public BigDecimal getSensorValue(){
        return value;
    }

    public LocalDateTime getTimeStamp(){
        return timeStamp;
    }
    @Override
    public String toString() {
        return "SensorData{" +
                "id=" + id +
                ", sensorType='" + sensorType + '\'' +
                ", timeStamp=" + timeStamp +
                ", value=" + value +
                '}';
    }
    // Getters and Setters
}
