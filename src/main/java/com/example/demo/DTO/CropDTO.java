package com.example.demo.DTO;

import java.time.LocalTime;

public class CropDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Double minTemperature;
    private Double maxTemperature;
    private Double minHumidity;
    private Double maxHumidity;
    private Double minSoilMoisture;
    private Double maxSoilMoisture;
    private LocalTime irrigationStartTime;
    private LocalTime irrigationEndTime;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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