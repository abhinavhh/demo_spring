package com.example.demo.Services;

import com.example.demo.Entities.Notification;
import com.example.demo.Entities.SensorData;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(String message, Users user) {
        Notification notification = new Notification(message, user);
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    /**
     * Checks the latest sensor data for each sensor type ("Soil Moisture", "Temperature", "Humidity")
     * for the given user and creates a notification if any threshold conditions are met.
     *
     * Thresholds:
     * - Soil Moisture: If value equals 100 -> "Soil is dry"
     *                  If value is 45 or below -> "High soil moisture detected"
     * - Temperature:   Above 38 -> "High temperature detected"
     *                  Below 22 -> "Low temperature detected"
     * - Humidity:      Above 85 -> "High humidity detected"
     *                  Below 50 -> "Low humidity detected"
     *
     * @param user The user to send the notification to.
     * @param sensorDataService The sensor data service to fetch the latest sensor readings.
     */
    public void checkLatestSensorDataAndNotify(Users user, SensorDataService sensorDataService) {
        // Retrieve the latest sensor reading for each sensor type using the service layer.
        SensorData soilData = sensorDataService.getLatestDataByUserAndSensorType(user.getId(), "Soil Moisture");
        SensorData tempData = sensorDataService.getLatestDataByUserAndSensorType(user.getId(), "Temperature");
        SensorData humidityData = sensorDataService.getLatestDataByUserAndSensorType(user.getId(), "Humidity");

        StringBuilder messageBuilder = new StringBuilder();

        // Soil Moisture Check
        if (soilData != null) {
            double soilMoisture = soilData.getValue();
            if (soilMoisture == 100) {
                messageBuilder.append("Soil is dry. ");
            } else if (soilMoisture <= 45) {
                messageBuilder.append("High soil moisture detected. ");
            }
        }

        // Temperature Check
        if (tempData != null) {
            double temperature = tempData.getValue();
            if (temperature > 38) {
                messageBuilder.append("High temperature detected. ");
            } else if (temperature < 22) {
                messageBuilder.append("Low temperature detected. ");
            }
        }

        // Humidity Check
        if (humidityData != null) {
            double humidity = humidityData.getValue();
            if (humidity > 85) {
                messageBuilder.append("High humidity detected. ");
            } else if (humidity < 50) {
                messageBuilder.append("Low humidity detected. ");
            }
        }

        // If any threshold condition is met, create a notification for the user.
        if (messageBuilder.length() > 0) {
            String notificationMessage = messageBuilder.toString().trim();
            createNotification(notificationMessage, user);
        }
    }
}
