package com.example.demo.Services;

import com.example.demo.Entities.Notification;
import com.example.demo.Entities.SensorData;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.Repositories.SensorDataRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
// import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SensorDataRepository sensorDataRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               SensorDataRepository sensorDataRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.sensorDataRepository = sensorDataRepository;
    }

    // Retrieves notifications for a given user sorted by creation time (latest first)
    public List<Notification> getNotificationsByUserId(Long userId) {
        try {
            return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving notifications for user: " + userId, e);
            throw e;
        }
    }

    // Marks a notification as read if it belongs to the given user
    public boolean markNotificationAsRead(Long notificationId, Long userId) {
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            if (notificationOpt.isPresent()) {
                Notification notification = notificationOpt.get();
                Users owner = notification.getUser();
                if (owner != null && owner.getId().equals(userId)) {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error marking notification as read for notificationId: " + notificationId, e);
        }
        return false;
    }

    // Creates a notification record for a user with a given message
    public void createNotification(Users user, String message) {
        try {
            // Check for existing notification in the last hour
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            List<Notification> existing = notificationRepository.findByUserAndMessageAndCreatedAtAfter(user, message, oneHourAgo);
            if (!existing.isEmpty()) {
                return;
            }

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(message);
            notification.setRead(false);
            notificationRepository.save(notification);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating notification for user: " + user.getId(), e);
        }
    }

    // Checks sensor data against thresholds using the userId passed from the front end.
    // Retrieves the latest sensor data and creates notifications if thresholds are violated.
    public List<Notification> checkSensorDataAndNotifyForUser(Long userId) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            LOGGER.severe("User with id " + userId + " not found.");
            return Collections.emptyList();
        }
        Users user = optionalUser.get();

        try {
            // Retrieve the latest sensor data (assuming one reading per sensor type)
            List<SensorData> sensorDataList = sensorDataRepository.findLatestSensorData(PageRequest.of(0, 4));

            for (SensorData data : sensorDataList) {
                String sensorType = data.getSensorType().toLowerCase();
                Double value = data.getValue();

                switch (sensorType) {
                    case "temperature":
                        if (value >= 38) {
                            createNotification(user, "High temperature alert: " + value + "°C");
                        } else if (value <= 25) {
                            createNotification(user, "Low temperature alert: " + value + "°C");
                        }
                        break;
                    case "humidity":
                        // Adjust thresholds as needed
                        if (value > 70) {
                            createNotification(user, "High humidity alert: " + value + "%");
                        } else if (value < 30) {
                            createNotification(user, "Low humidity alert: " + value + "%");
                        }
                        break;
                    case "soilmoisture":
                        // Soil moisture: 100 indicates dry, 50 indicates high moisture
                        if (value >= 100) {
                            createNotification(user, "Soil moisture alert: Soil is dry (" + value + ")");
                        } else if (value <= 50) {
                            createNotification(user, "Soil moisture alert: High soil moisture (" + value + ")");
                        }
                        break;
                    case "waterflow":
                        // Adjust thresholds as needed
                        if (value > 100) {
                            createNotification(user, "High water flow alert: " + value);
                        } else if (value < 10) {
                            createNotification(user, "Low water flow alert: " + value);
                        }
                        break;
                    default:
                        LOGGER.warning("Unknown sensor type: " + sensorType);
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking sensor data for user: " + userId, e);
        }
        return getNotificationsByUserId(userId);
    }

     // Optional: Uncomment to run this check automatically on a schedule (e.g., every 30 seconds).
//     @Scheduled(fixedRate = 30000)
//     public void scheduledSensorCheck() {
//         // For a scheduled check, you might iterate over all users.
//     }
}
