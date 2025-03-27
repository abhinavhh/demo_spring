package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "user_crop_irrigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "crop_id"})
})
@Getter
@Setter
public class UserCropIrrigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crops crop;

    @Column(nullable = false)
    private LocalTime irrigationStartTime;

    @Column(nullable = false)
    private LocalTime irrigationEndTime;

    public UserCropIrrigation() {}

    public UserCropIrrigation(Users user, Crops crop, LocalTime irrigationStartTime, LocalTime irrigationEndTime) {
        this.user = user;
        this.crop = crop;
        this.irrigationStartTime = irrigationStartTime;
        this.irrigationEndTime = irrigationEndTime;
    }
}
