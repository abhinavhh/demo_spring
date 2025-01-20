package com.example.demo.Components;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SensorDataWebSocketHandler implements WebSocketHandler {

    // Store connected WebSocket sessions
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // private String latestSensorData = "{}"; // Store the latest sensor data

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
        System.out.println("sensor Dara : " + data); // Update the latest sensor data

        // Broadcast the updated sensor data to all connected sessions
        broadcastSensorData(data);
    }

    // Send the latest sensor data to all connected clients
    private void broadcastSensorData(String data) {
        Flux.fromIterable(sessions)
            .filter(WebSocketSession::isOpen) // Ensure session is still open
            .flatMap(session -> session.send(Mono.just(session.textMessage(data)))
                .doOnError(e -> {
                    // Log the error and remove the session if sending fails
                    System.err.println("Error sending data to session: " + session.getId() + ", " + e.getMessage());
                    sessions.remove(session);
                }))
            .subscribe(); // Ensure the broadcast completes
    }
    
    
}
