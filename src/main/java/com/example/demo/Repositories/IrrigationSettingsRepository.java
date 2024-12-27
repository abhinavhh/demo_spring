package com.example.demo.Repositories;

import com.example.demo.Entities.IrrigationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IrrigationSettingsRepository extends JpaRepository<IrrigationSettings, Long> {
    List<IrrigationSettings> findByUserId(Long userId);
}
