package com.example.demo.Components;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.example.demo.Entities.SensorData;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.SensorDataRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.MosfetControlService;
import com.example.demo.Services.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final Map<String, Object> Crops = new ConcurrentHashMap<>();

    private final SensorDataRepository sensorDataRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final MosfetControlService mosfetControlService;

    // To throttle notifications (only once per interval)
    private long lastNotificationTime = 0;

    public SensorDataWebSocketHandler(SensorDataRepository sensorDataRepository,
                                      NotificationService notificationService,
                                      UserRepository userRepository,
                                      MosfetControlService mosfetControlService) {
        this.sensorDataRepository = sensorDataRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.mosfetControlService = mosfetControlService;
        
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

        // Schedule threshold check every 1 minute (adjust interval as needed)
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkThresholdsAndNotify();
            }
        }, 0, 60000);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        sessions.add(session);
        Mono<Void> receive = session.receive()
            .map(message -> message.getPayloadAsText())
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
            // Optionally store irrigation window if provided
            if (incomingData.containsKey("irrigationStartTime")) {
                sensorData.put("irrigationStartTime", incomingData.get("irrigationStartTime"));
            }
            if (incomingData.containsKey("irrigationEndTime")) {
                sensorData.put("irrigationEndTime", incomingData.get("irrigationEndTime"));
            }
            // Optionally store thresholds if provided (or they could be hard-coded)
            if (incomingData.containsKey("customMinTemperature")) {
                sensorData.put("customMinTemperature", incomingData.get("customMinTemperature"));
            }
            if (incomingData.containsKey("customMaxTemperature")) {
                sensorData.put("customMaxTemperature", incomingData.get("customMaxTemperature"));
            }
            if (incomingData.containsKey("customMinHumidity")) {
                sensorData.put("customMinHumidity", incomingData.get("customMinHumidity"));
            }
            if (incomingData.containsKey("customMaxHumidity")) {
                sensorData.put("customMaxHumidity", incomingData.get("customMaxHumidity"));
            }
            if (incomingData.containsKey("customMinSoilMoisture")) {
                sensorData.put("customMinSoilMoisture", incomingData.get("customMinSoilMoisture"));
            }
            if (incomingData.containsKey("customMaxSoilMoisture")) {
                sensorData.put("customMaxSoilMoisture", incomingData.get("customMaxSoilMoisture"));
            }
            // Store userId if provided
            if (incomingData.containsKey("userId")) {
                sensorData.put("userId", incomingData.get("userId"));
            }
            System.out.println("Updated sensorData: " + sensorData);
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
            });
            System.out.println("Sensor data saved to the database.");
        } catch (Exception e) {
            System.err.println("Error saving sensor data to the database: " + e.getMessage());
        }
    }

    // Check sensor thresholds and create a notification if any sensor is out-of-range,
    // but only perform the check between the irrigation start and end times.
    private void checkThresholdsAndNotify() {
        // Ensure irrigation window is set in sensorData (as strings in HH:mm format)
        Object startObj = Crops.get("irrigationStartTime");
        Object endObj = Crops.get("irrigationEndTime");
        if (startObj == null || endObj == null) {
            System.out.println("Irrigation window not set; skipping threshold check.");
            return;
        }
        try {
            LocalTime irrigationStart = LocalTime.parse((String) startObj);
            LocalTime irrigationEnd = LocalTime.parse((String) endObj);
            LocalTime nowTime = LocalTime.now();
            // Check if current time is within the irrigation window.
            if (nowTime.isBefore(irrigationStart) || nowTime.isAfter(irrigationEnd)) {
                System.out.println("Current time (" + nowTime + ") is outside the irrigation window (" 
                    + irrigationStart + " - " + irrigationEnd + "); skipping threshold check.");
                return;
            }
        } catch (Exception e) {
            System.err.println("Error parsing irrigation times: " + e.getMessage());
            return;
        }
        
        // Retrieve threshold values from sensorData (or use hard-coded defaults)
        double minTemp = sensorData.get("customMinTemperature") instanceof Number ? 
                ((Number) sensorData.get("customMinTemperature")).doubleValue() : 25;
        double maxTemp = sensorData.get("customMaxTemperature") instanceof Number ? 
                ((Number) sensorData.get("customMaxTemperature")).doubleValue() : 32;
        double minHumidity = sensorData.get("customMinHumidity") instanceof Number ? 
                ((Number) sensorData.get("customMinHumidity")).doubleValue() : 50;
        double maxHumidity = sensorData.get("customMaxHumidity") instanceof Number ? 
                ((Number) sensorData.get("customMaxHumidity")).doubleValue() : 60;
        double minSoil = sensorData.get("customMinSoilMoisture") instanceof Number ? 
                ((Number) sensorData.get("customMinSoilMoisture")).doubleValue() : 40;
        double maxSoil = sensorData.get("customMaxSoilMoisture") instanceof Number ? 
                ((Number) sensorData.get("customMaxSoilMoisture")).doubleValue() : 50;

        StringBuilder notificationMessage = new StringBuilder();

        // Check Temperature
        Object tempObj = sensorData.get("Temperature");
        if (tempObj instanceof Number) {
            double temp = ((Number) tempObj).doubleValue();
            if (temp < minTemp || temp > maxTemp) {
                notificationMessage.append("Temperature ").append(temp)
                  .append("°C is out of range (").append(minTemp)
                  .append("°C - ").append(maxTemp).append("°C). ");
            }
        }
        // Check Humidity
        Object humObj = sensorData.get("Humidity");
        if (humObj instanceof Number) {
            double hum = ((Number) humObj).doubleValue();
            if (hum < minHumidity || hum > maxHumidity) {
                notificationMessage.append("Humidity ").append(hum)
                  .append("% is out of range (").append(minHumidity)
                  .append("% - ").append(maxHumidity).append("%). ");
            }
        }
        // Check Soil Moisture
        Object soilObj = sensorData.get("SoilMoisture");
        if (soilObj instanceof Number) {
            double soil = ((Number) soilObj).doubleValue();
            if (soil < minSoil || soil > maxSoil) {
                notificationMessage.append("Soil Moisture ").append(soil)
                .append("% is out of range (").append(minSoil)
                .append("% - ").append(maxSoil).append("%). ");
                // Trigger the MOSFET if soil moisture is out-of-range
                try {
                    mosfetControlService.switchOnMosfet();
                } catch (Exception e) {
                    System.err.println("Error switching on MOSFET: " + e.getMessage());
                }
            }
        }

        if (notificationMessage.length() > 0) {
            long now = System.currentTimeMillis();
            // Only notify if at least 1 minute has passed since the last notification
            if (now - lastNotificationTime >= 60000) {
                String message = notificationMessage.toString();
                // Retrieve userId from sensorData if available
                Object userIdObj = Crops.get("userId");
                if (userIdObj instanceof Number) {
                    Long userId = ((Number) userIdObj).longValue();
                    Users user = userRepository.findById(userId).orElse(null);
                    if (user != null) {
                        notificationService.createNotification(message, user);
                        System.out.println("Notification sent: " + message);
                    } else {
                        System.out.println("User not found, notification not created");
                    }
                } else {
                    System.out.println("User ID not provided in sensor data, notification not created");
                }
                lastNotificationTime = now;
            }
        } else {
            System.out.println("All sensor values within thresholds.");
        }
    }
}
