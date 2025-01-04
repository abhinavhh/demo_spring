package com.example.demo.Services;


import com.example.demo.Repositories.IrrigationSettingsRepository;
import org.springframework.stereotype.Service;



@Service
public class IrrigationService {

    private final IrrigationSettingsRepository irrigationSettingsRepository;

    public IrrigationService(IrrigationSettingsRepository irrigationSettingsRepository) {
        this.irrigationSettingsRepository = irrigationSettingsRepository;
        
    }

   

    // public List<IrrigationSettings> getSettingsForCrop(Long cropId) {
    //     Long userId = userService.getCurrentUser().getId();
    //     return irrigationSettingsRepository.findByCropIdAndUserId(cropId, userId);
    // }

   

    public void deleteSettings(Long id) {
        irrigationSettingsRepository.deleteById(id);
    }
}
