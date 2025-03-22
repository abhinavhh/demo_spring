package com.example.demo.Services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MosfetControlService {

    private final RestTemplate restTemplate;
    // URL of the ESP32 endpoint; update with your actual IP/hostname.
    private final String esp32Url = "http://192.168.1.35"; 

    public MosfetControlService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String switchOnMosfet() {
        String url = esp32Url + "/mosfet/on";
        return restTemplate.getForObject(url, String.class);
    }

    public String switchOffMosfet() {
        String url = esp32Url + "/mosfet/off";
        return restTemplate.getForObject(url, String.class);
    }
}
