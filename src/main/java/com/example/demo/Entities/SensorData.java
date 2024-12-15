package com.example.demo.Entities;


// ENTITY : mark this class to a JPA entity this means it map to a table in the database

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity

// SensorData represents the table
public class SensorData{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("unused")
    private String sensorType;
    @SuppressWarnings("unused")
    private BigDecimal value;

    @CreationTimestamp
    private LocalDateTime timestamp;
}