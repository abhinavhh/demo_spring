package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "user_crops")
public class UserCrops {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapping to the user (assumes you have a Users entity)
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private Users user;

    // Mapping to the global Crop record
    @ManyToOne(optional = false)
    @JoinColumn(name = "crop_id")
    private Crops crop;

    // User-specific overrides – if not provided these can default to the Crop’s values
    private Double customMinTemperature;
    private Double customMaxTemperature;
    private Double customMinHumidity;
    private Double customMaxHumidity;
    private Double customMinSoilMoisture;
    private Double customMaxSoilMoisture;

    @Column(name = "irrigation_start_time")
    private LocalTime customIrrigationStartTime;

    @Column(name = "irrigation_end_time")
    private LocalTime customIrrigationEndTime;
}
