package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_crops")
public class UserCrops {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crops crop;

    private Double userMinTemperature;
    private Double userMaxTemperature;
    private Double userMinHumidity;
    private Double userMaxHumidity;
    private Double userMinSoilMoisture;
    private Double userMaxSoilMoisture;

    @Column(nullable = false)
    private Boolean isActive = true;

    public UserCrops() {}

    public UserCrops(Users user, Crops crop, Double minTemp, Double maxTemp, 
                     Double minHumidity, Double maxHumidity, 
                     Double minSoilMoisture, Double maxSoilMoisture) {
        this.user = user;
        this.crop = crop;
        this.userMinTemperature = minTemp;
        this.userMaxTemperature = maxTemp;
        this.userMinHumidity = minHumidity;
        this.userMaxHumidity = maxHumidity;
        this.userMinSoilMoisture = minSoilMoisture;
        this.userMaxSoilMoisture = maxSoilMoisture;
    }
}
