package com.example.demo.Services;

import com.example.demo.Entities.IrrigationSettings;
import com.example.demo.Repositories.IrrigationSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IrrigationService {

    private final IrrigationSettingsRepository irrigationSettingsRepository;
    private final UserService userService;

    public IrrigationService(IrrigationSettingsRepository irrigationSettingsRepository, UserService userService) {
        this.irrigationSettingsRepository = irrigationSettingsRepository;
        this.userService = userService;
    }

    public List<IrrigationSettings> getSettingsForUser() {
        Long userId = userService.getCurrentUser().getId();
        return irrigationSettingsRepository.findByUserId(userId);
    }

    // public List<IrrigationSettings> getSettingsForCrop(Long cropId) {
    //     Long userId = userService.getCurrentUser().getId();
    //     return irrigationSettingsRepository.findByCropIdAndUserId(cropId, userId);
    // }

    public IrrigationSettings addOrUpdateSettings(IrrigationSettings settings) {
        settings.setUser(userService.getCurrentUser());
        return irrigationSettingsRepository.save(settings);
    }

    public void deleteSettings(Long id) {
        irrigationSettingsRepository.deleteById(id);
    }
}
