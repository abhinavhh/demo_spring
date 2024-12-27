package com.example.demo.Controllers;

import com.example.demo.Entities.Crops;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.CropRepository;
import com.example.demo.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Crops> getCrops() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username).orElseThrow();
        return cropRepository.findByUserId(user.getId());
    }

    @PostMapping
    public String addCrop(@RequestBody Crops crop) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username).orElseThrow();
        crop.setUser(user);
        cropRepository.save(crop);
        return "Crop added successfully";
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<String> deleteCrop(@PathVariable Long id) {
    //     cropService.deleteCrop(id);
    //     return ResponseEntity.ok("Crop deleted successfully");
    // }
}
