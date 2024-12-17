package com.example.demo.Entities;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class IrrigationSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @SuppressWarnings("unused")
    private LocalTime starTime;
    @SuppressWarnings("unused")
    private LocalTime endTime;
    @SuppressWarnings("unused")
    private BigDecimal minMoisturalLevel;
}
