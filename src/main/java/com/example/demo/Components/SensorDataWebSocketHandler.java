package com.example.demo.Components;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.example.demo.Entities.SensorData;
import com.example.demo.Repositories.SensorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

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

    // private String latestSensorData = "{}"; // Store the latest sensor data
    private final Map<String, Object> sensorData = new ConcurrentHashMap<>();
    
    public SensorDataWebSocketHandler(){
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
        }, 0, 60000); // 60000ms = 1 minute
    }
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Add the session to the set of active sessions
        sessions.add(session);
        
        // Handle incoming messages from the client
        Mono<Void> receive = session.receive()
            .map(message -> message.getPayloadAsText()) // Convert to text
            .doOnNext(this::handleIncomingData) // Process incoming data
            .then(); // Close when done

        // Monitor and remove session upon close
        session.closeStatus()
            .doOnTerminate(() -> {
                sessions.remove(session);
                System.out.println("Session closed and removed: " + session.getId());
            })
            .subscribe();

        return receive;
    }

    // Process incoming sensor data
    private void handleIncomingData(String data) {
        // System.out.println("sensor Data : " + data); // Update the latest sensor data

        // // Broadcast the updated sensor data to all connected sessions
        // broadcastSensorData(data);
        try {
            // Parse incoming sensor data and update the map
            Map<String, Object> incomingData = new ObjectMapper().readValue(data, Map.class);
            sensorData.put((String) incomingData.get("sensorType"), incomingData.get("value"));
        } catch (Exception e) {
            System.err.println("Error processing incoming data: " + e.getMessage());
        }
    }

    // Send the latest sensor data to all connected clients
    private void broadcastSensorData() {
        // Flux.fromIterable(sessions)
        //     .filter(WebSocketSession::isOpen) // Ensure session is still open
        //     .flatMap(session -> session.send(Mono.just(session.textMessage(data)))
        //         .doOnError(e -> {
        //             // Log the error and remove the session if sending fails
        //             System.err.println("Error sending data to session: " + session.getId() + ", " + e.getMessage());
        //             sessions.remove(session);
        //         }))
        //     .subscribe(); // Ensure the broadcast completes
        try {
            String combinedData = new ObjectMapper().writeValueAsString(sensorData);
            Flux.fromIterable(sessions)
                .filter(WebSocketSession::isOpen)
                .flatMap(session -> session.send(Mono.just(session.textMessage(combinedData)))
                    .doOnError(_ -> sessions.remove(session)))
                .subscribe();
        } catch (Exception e) {
            System.err.println("Error broadcasting sensor data: " + e.getMessage());
        }
    }
    private SensorDataRepository sensorDataRepository;
    private void saveSensorDataToDatabase() {
    try {
        sensorData.forEach((sensorType, value) -> {
            SensorData entity = new SensorData();
            entity.setSensorType(sensorType);

            // Convert value to double (ensure it's castable)
            if (value instanceof Number) {
                entity.setValue(((Number) value).doubleValue());
            } else {
                System.err.println("Invalid value type for sensor: " + sensorType);
                return; // Skip saving this entry if value is not a valid number
            }

            // Set the timestamp to the current LocalDateTime
            entity.setTimestamp(LocalDateTime.now());

            // Save to the database
            sensorDataRepository.save(entity);
        });
        System.out.println("Sensor data saved to the database.");
    } catch (Exception e) {
        System.err.println("Error saving sensor data to the database: " + e.getMessage());
    }
    }

    
    
}
