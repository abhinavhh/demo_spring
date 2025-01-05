package com.example.demo.Components;


import com.example.demo.Services.SensorDataService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class SensorDataWebSocketHandler implements WebSocketHandler {

    private final SensorDataService sensorDataService;

    public SensorDataWebSocketHandler(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<String> sensorDataStream = sensorDataService
            .getAllSensorDataStream()
            .map(data -> String.format("{\"sensorType\":\"%s\",\"value\":%s}", data.getSensorType(), data.getValue()))
            .delayElements(Duration.ofSeconds(2))// Send updates every 2 seconds
             .repeat();

        return session.send(sensorDataStream.map(session::textMessage));
    }
}
