package com.example.demo.Components;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.example.demo.Entities.SensorData;
import com.example.demo.Repositories.SensorDataRepository;
import com.example.demo.Services.NotificationService;
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

    // Latest sensor data (keyed by sensorType)
    private final Map<String, Object> sensorData = new ConcurrentHashMap<>();

    private final SensorDataRepository sensorDataRepository;
    private final NotificationService notificationService;

    // To throttle notifications (only once every 30 minutes)
    private long lastNotificationTime = 0;

    public SensorDataWebSocketHandler(SensorDataRepository sensorDataRepository, NotificationService notificationService) {
        this.sensorDataRepository = sensorDataRepository;
        this.notificationService = notificationService;
        
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
        }, 0, 60000); // 60000ms = 1 minute

        // Schedule threshold check and notification every 30 minutes
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkThresholdsAndNotify();
            }
        }, 0, 30 * 60 * 1000); // 30 minutes
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Add session to active sessions
        sessions.add(session);

        // Process incoming messages from the client
        Mono<Void> receive = session.receive()
            .map(message -> message.getPayloadAsText())
            .doOnNext(this::handleIncomingData)
            .then();

        // Remove session when closed
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

    // Process incoming sensor data (expects JSON with "sensorType" and "value")
    private void handleIncomingData(String data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> incomingData = new ObjectMapper().readValue(data, Map.class);
            String type = (String) incomingData.get("sensorType");
            Object value = incomingData.get("value");
            if (type != null && value != null) {
                sensorData.put(type, value);
            }
            System.out.println("Updated sensorData: " + sensorData);
        } catch (Exception e) {
            System.err.println("Error processing incoming data: " + e.getMessage());
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
            });
            System.out.println("Sensor data saved to the database.");
        } catch (Exception e) {
            System.err.println("Error saving sensor data to the database: " + e.getMessage());
        }
    }

    // Check sensor thresholds and create a notification if any sensor is out-of-range.
    private void checkThresholdsAndNotify() {
        // Hard-coded thresholds for demonstration. In production, retrieve these from user-specific configuration.
        double minTemp = 25, maxTemp = 32;
        double minHumidity = 50, maxHumidity = 60;
        double minSoil = 40, maxSoil = 50;

        StringBuilder notificationMessage = new StringBuilder();

        Object tempObj = sensorData.get("Temperature");
        if (tempObj instanceof Number) {
            double temp = ((Number) tempObj).doubleValue();
            if (temp < minTemp || temp > maxTemp) {
                notificationMessage.append("Temperature ").append(temp)
                  .append("°C is out of range (").append(minTemp)
                  .append("°C - ").append(maxTemp).append("°C). ");
            }
        }
        Object humObj = sensorData.get("Humidity");
        if (humObj instanceof Number) {
            double hum = ((Number) humObj).doubleValue();
            if (hum < minHumidity || hum > maxHumidity) {
                notificationMessage.append("Humidity ").append(hum)
                  .append("% is out of range (").append(minHumidity)
                  .append("% - ").append(maxHumidity).append("%). ");
            }
        }
        Object soilObj = sensorData.get("SoilMoisture");
        if (soilObj instanceof Number) {
            double soil = ((Number) soilObj).doubleValue();
            if (soil < minSoil || soil > maxSoil) {
                notificationMessage.append("Soil Moisture ").append(soil)
                  .append("% is out of range (").append(minSoil)
                  .append("% - ").append(maxSoil).append("%). ");
            }
        }

        if (notificationMessage.length() > 0) {
            long now = System.currentTimeMillis();
            // Only notify if 30 minutes have passed since last notification
            if (now - lastNotificationTime >= 60000) {
                notificationService.createNotification(notificationMessage.toString());
                System.out.println("Notification sent: " + notificationMessage.toString());
                lastNotificationTime = now;
            }
        } else {
            System.out.println("All sensor values within thresholds.");
        }
    }
}
