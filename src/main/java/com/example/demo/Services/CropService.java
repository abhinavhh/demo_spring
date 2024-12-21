package com.example.demo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.Entities.Crop;
import com.example.demo.Repositories.CropRepository;

@Service
public class CropService {

    private final CropRepository cropRepository;

    public CropService(CropRepository cropRepository){
        this.cropRepository = cropRepository;
    }

    public List<Crop> getAllCrops(){
        return cropRepository.findAll();
    }

    public Crop addCrop(Crop crop){
        return cropRepository.save(crop);
    }
    
    public void deleteCrop(Long id){
        cropRepository.deleteById(id);
    }
}
