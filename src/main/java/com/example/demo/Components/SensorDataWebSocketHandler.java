package com.example.demo.Components;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.example.demo.Entities.SensorData;

import com.example.demo.Repositories.SensorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SensorDataWebSocketHandler implements WebSocketHandler {

    // Store connected WebSocket sessions
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    // Latest sensor data (keyed by sensorType, plus irrigation settings if available)
    private final Map<String, Object> sensorData = new ConcurrentHashMap<>();

    private final SensorDataRepository sensorDataRepository;


    public SensorDataWebSocketHandler(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
        
        // Schedule broadcasting sensor data every 5 seconds
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                broadcastSensorData();
            }
        }, 0, 5000);

        // Schedule database saving every 1 minute
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveSensorDataToDatabase();
            }
        }, 0, 10000); // 60000ms = 1 minute

    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        sessions.add(session);
        Mono<Void> receive = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(this::handleIncomingData)
            .then();

        session.closeStatus()
            .doOnNext(status -> { 
                System.out.println("Session closed with status: " + status);
                sessions.remove(session);
                System.out.println("Session removed: " + session.getId());
            })
            .doOnError(error -> System.err.println("Error closing session: " + error.getMessage()))
            .subscribe();

        return receive;
    }

    // Process incoming sensor data (expects JSON with "sensorType", "value" and optionally irrigation settings and "userId")
    private void handleIncomingData(String data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> incomingData = new ObjectMapper().readValue(data, Map.class);
            String type = (String) incomingData.get("sensorType");
            Object value = incomingData.get("value");
            if (type != null && value != null) {
                sensorData.put(type, value);
            }
        } catch (Exception e) {
            System.err.println("Error processing incoming data: ");
        }
    }

    // Broadcast sensor data to all connected sessions
    private void broadcastSensorData() {
        try {
            String combinedData = new ObjectMapper().writeValueAsString(sensorData);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.send(Mono.just(session.textMessage(combinedData)))
                        .doOnError(e -> {
                            System.err.println("Error sending to session " + session.getId() + ": " + e.getMessage());
                            sessions.remove(session);
                        })
                        .subscribe();
                }
            }
        } catch (Exception e) {
            System.err.println("Error broadcasting sensor data: " + e.getMessage());
        }
    }

    // Save sensor data to the database
    private void saveSensorDataToDatabase() {
        try {
            sensorData.forEach((sensorType, value) -> {
                // Skip keys that are not actual sensor readings
                if ("userId".equals(sensorType) || "irrigationStartTime".equals(sensorType) || "irrigationEndTime".equals(sensorType)
                    || sensorType.startsWith("custom")) return;
                SensorData entity = new SensorData();
                entity.setSensorType(sensorType);
                if (value instanceof Number) {
                    entity.setValue(((Number) value).doubleValue());
                } else {
                    System.err.println("Invalid value type for sensor: " + sensorType);
                    return;
                }
                entity.setTimestamp(LocalDateTime.now());
                sensorDataRepository.save(entity);
                System.out.println("Sensor Data saved to the Database");
            });
        } catch (Exception e) {
            System.err.println("Error saving sensor data to the database: " + e.getMessage());
        }
    }
}
