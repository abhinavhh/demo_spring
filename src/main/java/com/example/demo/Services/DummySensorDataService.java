package com.example.demo.Services;

import com.example.demo.Entities.SensorData;
import com.example.demo.Entities.Users;
import com.example.demo.Repositories.SensorDataRepository;
import com.example.demo.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DummySensorDataService {

    private final SensorDataRepository sensorRepo;
    private final UserRepository userRepo;
    private final Random random = new Random();

    public void generateDummyData(Long userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> sensorTypes = List.of("Temperature", "Humidity", "Soil Moisture");

        for (String type : sensorTypes) {
            for (int i = 0; i < 20; i++) {
                SensorData data = new SensorData();
                data.setUser(user);
                data.setSensorType(type);

                // Assign random value
                if (type.equals("Temperature")) {
                    data.setValue(15 + random.nextDouble() * 15); // 15–30 °C
                } else if (type.equals("Humidity")) {
                    data.setValue(40 + random.nextDouble() * 30); // 40–70 %
                } else if (type.equals("Soil Moisture")) {
                    data.setValue(20 + random.nextDouble() * 50); // 20–70 %
                }

                // Random timestamp within last 60 days
                data.setTimestamp(LocalDateTime.now()
                        .minusDays(random.nextInt(60))
                        .minusHours(random.nextInt(24)));

                sensorRepo.save(data);
            }
        }
    }
}
