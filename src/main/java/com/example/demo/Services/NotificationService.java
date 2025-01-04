// package com.example.demo.Services;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.example.demo.Entities.Notifications;
// import com.example.demo.Entities.SensorData;
// import com.example.demo.Entities.Users;
// import com.example.demo.Repositories.NotificationRepository;
// import com.example.demo.Repositories.SensorDataRepository;

// import java.time.LocalDateTime;
// import java.util.List;

// @Service
// public class NotificationService {

//     @Autowired
//     private NotificationRepository notificationRepository;

//     @Autowired
//     private SensorDataRepository sensorDataRepository;

//     public void analyzeSensorData(Users user) {

//         for (SensorData data : latestData) {
//             if ("temperature".equals(data.getSensorType()) && data.getValue() > 35.0) {
//                 generateNotification(user, "High temperature detected: " + data.getValue() + "°C");
//             } else if ("soilMoisture".equals(data.getSensorType()) && data.getValue() > 80.0) {
//                 generateNotification(user, "High soil moisture detected: " + data.getValue() + "%");
//             }
//         }
//     }

//     private void generateNotification(Users user, String message) {
//         Notifications notification = new Notifications();
//         notification.setUser(user);
//         notification.setMessage(message);
//         notification.setTimestamp(LocalDateTime.now());
//         notificationRepository.save(notification);
//     }
// }
