package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "crops")
public class Crops {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Users user;

    private String name;

    private String description;

    private String imageUrl;

    private Double minTemperature;
    private Double maxTemperature;
    private Double minHumidity;
    private Double maxHumidity;
    private Double minSoilMoisture;
    private Double maxSoilMoisture;

    @Column(name = "irrigation_start_time")
    private LocalTime irrigationStartTime;

    @Column(name = "irrigation_end_time")
    private LocalTime irrigationEndTime;

    public Crops() {}

    public Crops(String name, Double minTemperature, Double maxTemperature, Double minHumidity, 
                 Double maxHumidity, Double minSoilMoisture, Double maxSoilMoisture, 
                 Users user, LocalTime irrigationStartTime, LocalTime irrigationEndTime) {
        this.name = name;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minSoilMoisture = minSoilMoisture;
        this.maxSoilMoisture = maxSoilMoisture;
        this.user = user;
        this.irrigationStartTime = irrigationStartTime;
        this.irrigationEndTime = irrigationEndTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Double getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(Double minHumidity) {
        this.minHumidity = minHumidity;
    }

    public Double getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(Double maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public Double getMinSoilMoisture() {
        return minSoilMoisture;
    }

    public void setMinSoilMoisture(Double minSoilMoisture) {
        this.minSoilMoisture = minSoilMoisture;
    }

    public Double getMaxSoilMoisture() {
        return maxSoilMoisture;
    }

    public void setMaxSoilMoisture(Double maxSoilMoisture) {
        this.maxSoilMoisture = maxSoilMoisture;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalTime getIrrigationStartTime() {
        return irrigationStartTime;
    }

    public void setIrrigationStartTime(LocalTime irrigationStartTime) {
        this.irrigationStartTime = irrigationStartTime;
    }

    public LocalTime getIrrigationEndTime() {
        return irrigationEndTime;
    }

    public void setIrrigationEndTime(LocalTime irrigationEndTime) {
        this.irrigationEndTime = irrigationEndTime;
    }
}
