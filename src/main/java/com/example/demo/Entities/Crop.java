package com.example.demo.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // name of the crop
    private Double minTempearture;
    private Double maxTemperature;
    private Double minHumidity;
    private Double maxHumidity;
    private Double minSoilMoisture;
    private Double maxSoilMoisture; 

    //Constructor
    public Crop(){}

    public Crop(String name, Double minTemperature , Double maxTemperature, Double minHumidity, Double maxHumidity, Double minSoilMoisture, Double maxSoilMoisture){
        
        this.name = name;
        this.minTempearture = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minSoilMoisture = minSoilMoisture;
        this.maxSoilMoisture = maxSoilMoisture;
    }
    //Getters and Setters
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getname(){
        return name;
    }
    public void setname(String name){
        this.name = name;
    }
    public Double getMinTemperature(){
        return minTempearture;
    }
    public void setMinTemperature(Double minTemperature){
        this.minTempearture = minTemperature;
    }
    public Double getMaxTemperature(){
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
}
