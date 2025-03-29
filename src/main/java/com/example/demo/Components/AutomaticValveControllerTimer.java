package com.example.demo.Components;

import com.example.demo.Services.IrrigationService;
import java.util.Timer;
import java.util.TimerTask;

public class AutomaticValveControllerTimer {

    private Timer timer;
    private final IrrigationService irrigationService;
    private final Long userId;
    private final Long cropId;

    // A static field to keep track of the current active timer.
    private static AutomaticValveControllerTimer currentTimer;

    public AutomaticValveControllerTimer(IrrigationService irrigationService, Long userId, Long cropId) {
        this.irrigationService = irrigationService;
        this.userId = userId;
        this.cropId = cropId;
        this.timer = new Timer();
    }

    // Synchronized static method to stop any existing timer and start a new one.
    public static synchronized void startNewTimer(IrrigationService irrigationService, Long userId, Long cropId) {
        // Stop the old timer if one exists.
        if (currentTimer != null) {
            currentTimer.stop();
            System.out.println("Stopped old timer for userId: " + currentTimer.userId + ", cropId: " + currentTimer.cropId);
        }
        // Create and assign the new timer.
        currentTimer = new AutomaticValveControllerTimer(irrigationService, userId, cropId);
        currentTimer.start();
    }

    public void start() {
        // Schedule the task to run immediately and then every 20 seconds (20000ms)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String result = irrigationService.automaticValveControl(userId, cropId);
                System.out.println("Automatic Valve Control Timer Result for userId " + userId + ", cropId " + cropId + ": " + result);
            }
        }, 0, 20000);
    }

    public void stop() {
        timer.cancel();
        // Clear the current timer if this instance is the one active.
        if (currentTimer == this) {
            currentTimer = null;
        }
        System.out.println("Timer stopped for userId " + userId + " and cropId " + cropId);
    }
}
