package com.example.demo.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_crops", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "crop_id"})
})

public class UserCrop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crops crop;

    private Double minTemperature;
    private Double maxTemperature;
    private Double minHumidity;
    private Double maxHumidity;
    private Double minSoilMoisture;
    private Double maxSoilMoisture;

    public UserCrop() {}

    public UserCrop(Users user, Crops crop, Double minTemperature, Double maxTemperature, Double minHumidity, 
    Double maxHumidity, Double minSoilMoisture, Double maxSoilMoisture) {
        this.user = user;
        this.crop = crop;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minSoilMoisture = minSoilMoisture;
        this.maxSoilMoisture = maxSoilMoisture;
    }


     public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public Crops getCrops(){
        return crop;
    }

    public void setCrops(Crops crop){
        this.crop = crop;
    }
}
