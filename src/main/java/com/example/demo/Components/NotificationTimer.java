package com.example.demo.Components;

import com.example.demo.Entities.Notification;
import com.example.demo.Services.NotificationService;
import java.util.List;
import java.util.concurrent.*;

public class NotificationTimer {

    private static final ConcurrentHashMap<Long, ScheduledFuture<?>> currentTimers = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public static List<Notification> startNewTimer(NotificationService notificationService, Long userId) {
        // Perform initial check synchronously
        List<Notification> notifications = notificationService.checkSensorDataAndNotifyForUser(userId);

        // Cancel existing timer for the user if present
        ScheduledFuture<?> existingFuture = currentTimers.get(userId);
        if (existingFuture != null) {
            existingFuture.cancel(false);
        }

        // Schedule periodic checks every 20 seconds starting after 20 seconds
        ScheduledFuture<?> newFuture = scheduler.scheduleAtFixedRate(() -> {
            notificationService.checkSensorDataAndNotifyForUser(userId);
        }, 20, 20, TimeUnit.SECONDS);

        currentTimers.put(userId, newFuture);

        return notifications;
    }

    public static void stopTimer(Long userId) {
        ScheduledFuture<?> future = currentTimers.get(userId);
        if (future != null) {
            future.cancel(false);
            currentTimers.remove(userId);
        }
    }
}