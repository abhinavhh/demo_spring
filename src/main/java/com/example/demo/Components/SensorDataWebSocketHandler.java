package com.example.demo.Components;

import com.example.demo.Services.SensorDataService;


import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@Configuration
public class SensorDataWebSocketHandler implements WebSocketHandler {
    private final SensorDataService sensorDataService;
    private final Random random = new Random();

    public SensorDataWebSocketHandler(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    private Flux<String> createSensorStream(String sensorType, int minValue, int maxValue) {
        return Flux.interval(Duration.ofSeconds(10))
            .map(tick -> String.format(
                "{\"timestamp\":\"%s\",\"sensorType\":\"%s\",\"value\":%d}",
                LocalDateTime.now(),
                sensorType,
                minValue + random.nextInt(maxValue - minValue + 1)
            ));
    }

    @SuppressWarnings("null")
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Create streams for each sensor type with appropriate ranges
        Flux<String> temperatureStream = createSensorStream("Temperature", 20, 40);  // 20-40°C
        Flux<String> humidityStream = createSensorStream("Humidity", 30, 90);       // 30-90%
        Flux<String> soilMoistureStream = createSensorStream("SoilMoisture", 0, 100); // 0-100%

        // Combine with your existing sensor data stream if needed
        Flux<String> sensorDataStream = sensorDataService
            .getAllSensorDataStream()
            .map(data -> String.format(
                "{\"timestamp\":\"%s\",\"sensorType\":\"%s\",\"value\":%s}",
                LocalDateTime.now(),
                data.getSensorType(),
                data.getValue()
            ))
            .delayElements(Duration.ofSeconds(10));

        // Merge all streams
        Flux<String> combinedStream = Flux.merge(
            temperatureStream,
            humidityStream,
            soilMoistureStream,
            sensorDataStream
        ).repeat();

        return session.send(combinedStream.map(session::textMessage));
    }

}