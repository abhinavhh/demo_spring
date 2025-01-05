package com.example.demo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.example.demo.Components.SensorDataWebSocketHandler;

import java.util.Map;

@Configuration
public class WebSocketConfig {
    private final SensorDataWebSocketHandler sensorDataWebSocketHandler;

    public WebSocketConfig(SensorDataWebSocketHandler sensorDataWebSocketHandler) {
        this.sensorDataWebSocketHandler = sensorDataWebSocketHandler;
    }

    @Bean
    public SimpleUrlHandlerMapping webSocketHandlerMapping() {
        return new SimpleUrlHandlerMapping(Map.of("/ws/sensor-data", sensorDataWebSocketHandler), 1);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}