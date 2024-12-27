package com.example.demo.Services;

import com.example.demo.Entities.Crops;
import com.example.demo.Repositories.CropRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final UserService userService;

    public CropService(CropRepository cropRepository, UserService userService) {
        this.cropRepository = cropRepository;
        this.userService = userService;
    }

    public List<Crops> getCropsForCurrentUser() {
        Long userId = userService.getCurrentUser().getId();
        return cropRepository.findByUserId(userId);
    }

    public Crops addCropForCurrentUser(Crops crop) {
        crop.setUser(userService.getCurrentUser());
        return cropRepository.save(crop);
    }

    public void deleteCrop(Long id) {
        Crops crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));
        cropRepository.delete(crop);
    }
}
