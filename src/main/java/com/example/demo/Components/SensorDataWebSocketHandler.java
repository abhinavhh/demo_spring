package com.example.demo.Components;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
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

@Component
public class SensorDataWebSocketHandler implements WebSocketHandler {

    // ✅ Store sessions per Device ID
    private final Map<String, WebSocketSession> deviceSessions = new ConcurrentHashMap<>();
    private final Map<String, Object> sensorData = new ConcurrentHashMap<>();
    private final SensorDataRepository sensorDataRepository;

    public SensorDataWebSocketHandler(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;

        // Broadcast every 5 seconds
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                broadcastSensorData();
            }
        }, 0, 5000);

        // Save data every 1 minute
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveSensorDataToDatabase();
            }
        }, 0, 60000);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // ✅ Extract Device ID from WebSocket URL
        String path = session.getHandshakeInfo().getUri().getPath();
        String deviceId = path.substring(path.lastIndexOf('/') + 1);

        System.out.println("New connection from Device ID: " + deviceId);

        // ✅ Store session against Device ID
        deviceSessions.put(deviceId, session);

        // Handle incoming messages
        Mono<Void> receive = session.receive()
            .map(message -> message.getPayloadAsText())
            .doOnNext(data -> handleIncomingData(deviceId, data))
            .then();

        // Handle session close
        session.closeStatus()
            .doOnTerminate(() -> {
                deviceSessions.remove(deviceId);
                System.out.println("Session closed for Device ID: " + deviceId);
            })
            .subscribe();

        return receive;
    }

    // ✅ Handle incoming sensor data
    private void handleIncomingData(String deviceId, String data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> incomingData = new ObjectMapper().readValue(data, Map.class);
            sensorData.put(deviceId + ":" + incomingData.get("sensorType"), incomingData.get("value"));

            System.out.println("Received from Device " + deviceId + ": " + sensorData);
        } catch (Exception e) {
            System.err.println("Error processing data: " + e.getMessage());
        }
    }

    // ✅ Broadcast sensor data to correct Device ID only
    private void broadcastSensorData() {
        for (Map.Entry<String, WebSocketSession> entry : deviceSessions.entrySet()) {
            String deviceId = entry.getKey();
            WebSocketSession session = entry.getValue();

            try {
                String data = new ObjectMapper().writeValueAsString(getDeviceData(deviceId));

                if (session.isOpen()) {
                    session.send(Mono.just(session.textMessage(data)))
                        .doOnError(e -> deviceSessions.remove(deviceId))
                        .subscribe();
                }
            } catch (Exception e) {
                System.err.println("Error broadcasting: " + e.getMessage());
            }
        }
    }

    // ✅ Get sensor data for a specific Device ID
    private Map<String, Object> getDeviceData(String deviceId) {
        Map<String, Object> deviceData = new ConcurrentHashMap<>();
        sensorData.forEach((key, value) -> {
            if (key.startsWith(deviceId)) {
                deviceData.put(key.split(":")[1], value);
            }
        });
        return deviceData;
    }

    // ✅ Save sensor data to database
    private void saveSensorDataToDatabase() {
        sensorData.forEach((key, value) -> {
            String[] parts = key.split(":");
            String deviceId = parts[0];
            String sensorType = parts[1];

            SensorData entity = new SensorData();
            entity.setSensorType(sensorType);
            entity.setValue(Double.parseDouble(value.toString()));
            entity.setTimestamp(LocalDateTime.now());
            sensorDataRepository.save(entity);
        });

        System.out.println("Sensor data saved to DB.");
    }
}
