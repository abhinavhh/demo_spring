package com.example.demo.Components;




import com.example.demo.Services.SensorDataService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
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
        return session.send(
                sensorDataService.getAllSensorDataStream()
                        .map(data -> session.textMessage(data.toString()))
                        .delayElements(Duration.ofSeconds(2)) // Simulate real-time updates
        );
    }
}
