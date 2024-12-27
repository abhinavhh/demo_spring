package com.example.demo.Entities;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
public class IrrigationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crops crop;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private LocalTime startTime;
    private LocalTime endTime;

    private Boolean isManualControlEnabled = false;

    // Constructors
    public IrrigationSettings() {}

    public IrrigationSettings(Crops crop, LocalTime startTime, LocalTime endTime, Boolean isManualControlEnabled, Users user) {
        this.crop = crop;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isManualControlEnabled = isManualControlEnabled;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Crops getCrop() {
        return crop;
    }

    public void setCrop(Crops crop) {
        this.crop = crop;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsManualControlEnabled() {
        return isManualControlEnabled;
    }

    public void setIsManualControlEnabled(Boolean isManualControlEnabled) {
        this.isManualControlEnabled = isManualControlEnabled;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
