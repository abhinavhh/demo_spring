package com.example.demo.Repositories;

import com.example.demo.Entities.IrrigationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IrrigationSettingsRepository extends JpaRepository<IrrigationSettings, Long> {
    Optional<IrrigationSettings> findByCropIdAndUserId(Long cropId, Long userId);
}
