package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.IrrigationSettings;

@Repository
public interface IrrigationSettingsRepository extends JpaRepository<IrrigationSettings, Long>{
    
    
}
