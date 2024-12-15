package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entities.IrrigationSettings;

public interface IrrigationSettingsRepository extends JpaRepository<IrrigationSettings, Long>{
    
    
}
