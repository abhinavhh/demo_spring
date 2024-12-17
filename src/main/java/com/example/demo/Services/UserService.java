package com.example.demo.Services;

import org.springframework.stereotype.Service;

import com.example.demo.Entities.IrrigationSettings;
import com.example.demo.Repositories.IrrigationSettingsRepository;

@Service
public class UserService {
    private final IrrigationSettingsRepository irrigationSettingsRepository;
    public UserService(IrrigationSettingsRepository irrigationSettingsRepository){
        this.irrigationSettingsRepository = irrigationSettingsRepository;
    }
    public void updateUserSettings(IrrigationSettings settings){
        irrigationSettingsRepository.save(settings);
    }
}
