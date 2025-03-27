package com.example.demo.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class MosfetControlService {

    private final RestTemplate restTemplate;
    // URL of the ESP32 endpoint; update with your actual IP/hostname.
    private final String esp32Url = "http://192.168.1.37";
    private static final Logger logger = LoggerFactory.getLogger(MosfetControlService.class);

    public MosfetControlService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String switchOnMosfet() {
        String url = esp32Url + "/mosfet/on";
        try {
            logger.info("Sending request to switch on MOSFET: {}", url);
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException ex) {
            logger.error("Error switching on MOSFET: {}", ex.getMessage());
            return "Error switching on MOSFET";
        }
    }

    public String switchOffMosfet() {
        String url = esp32Url + "/mosfet/off";
        try {
            logger.info("Sending request to switch off MOSFET: {}", url);
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException ex) {
            logger.error("Error switching off MOSFET: {}", ex.getMessage());
            return "Error switching off MOSFET";
        }
    }
}
