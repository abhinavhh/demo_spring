package com.example.demo.Entities;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
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

    // Field to store manual control state
    @Column(name = "manual_open_enabled")
    private Boolean manualOpenEnabled;

    @Column(name = "manual_close_enabled")
    private Boolean manualCloseEnabled;
    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Crops getCrop() {
        return crop;
    }

    public void setCrop(Crops crop) {
        this.crop = crop;
    }

    public Double getCustomMinTemperature() {
        return customMinTemperature;
    }

    public void setCustomMinTemperature(Double customMinTemperature) {
        this.customMinTemperature = customMinTemperature;
    }

    public Double getCustomMaxTemperature() {
        return customMaxTemperature;
    }

    public void setCustomMaxTemperature(Double customMaxTemperature) {
        this.customMaxTemperature = customMaxTemperature;
    }

    public Double getCustomMinHumidity() {
        return customMinHumidity;
    }

    public void setCustomMinHumidity(Double customMinHumidity) {
        this.customMinHumidity = customMinHumidity;
    }

    public Double getCustomMaxHumidity() {
        return customMaxHumidity;
    }

    public void setCustomMaxHumidity(Double customMaxHumidity) {
        this.customMaxHumidity = customMaxHumidity;
    }

    public Double getCustomMinSoilMoisture() {
        return customMinSoilMoisture;
    }

    public void setCustomMinSoilMoisture(Double customMinSoilMoisture) {
        this.customMinSoilMoisture = customMinSoilMoisture;
    }

    public Double getCustomMaxSoilMoisture() {
        return customMaxSoilMoisture;
    }

    public void setCustomMaxSoilMoisture(Double customMaxSoilMoisture) {
        this.customMaxSoilMoisture = customMaxSoilMoisture;
    }

    public LocalTime getCustomIrrigationStartTime() {
        return customIrrigationStartTime;
    }

    public void setCustomIrrigationStartTime(LocalTime customIrrigationStartTime) {
        this.customIrrigationStartTime = customIrrigationStartTime;
    }

    public LocalTime getCustomIrrigationEndTime() {
        return customIrrigationEndTime;
    }

    public void setCustomIrrigationEndTime(LocalTime customIrrigationEndTime) {
        this.customIrrigationEndTime = customIrrigationEndTime;
    }

    public Boolean isManualOpenEnabled() {
        return manualOpenEnabled;
    }

    public void setManualOpenEnabled(Boolean manualOpenEnabled) {
        this.manualOpenEnabled = manualOpenEnabled;
    }

    public Boolean isManualCloseEnabled() {
        return manualCloseEnabled;
    }

    public void setManualCloseEnabled(Boolean manualCloseEnabled) {
        this.manualCloseEnabled = manualCloseEnabled;
    }
}
