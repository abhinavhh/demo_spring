package com.example.demo.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entities.IrrigationSettings;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.IrrigationSettingsRepository;
import com.example.demo.Repositories.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/irrigation")
public class IrrigationController {

    @Autowired
    private IrrigationSettingsRepository irrigationSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    private Users getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @GetMapping("/settings")
    public List<IrrigationSettings> getIrrigationSettings() {
        Users user = getCurrentUser();
        return irrigationSettingsRepository.findByUserId(user.getId());
    }

    @PostMapping("/settings")
    public String addIrrigationSetting(@RequestBody IrrigationSettings setting) {
        Users user = getCurrentUser();
        setting.setUser(user);
        irrigationSettingsRepository.save(setting);
        return "Irrigation setting added successfully";
    }
}
