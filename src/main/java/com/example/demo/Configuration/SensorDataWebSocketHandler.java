package com.example.demo.Configuration;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class SensorDataWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        if ("getLatestData".equals(payload)) {
            // Respond with mock or real sensor data
            String response = """
                {"sensorType": "temperature", "value": 25.5, "timeStamp": "2024-12-17T10:00:00"}
                """;
            session.sendMessage(new TextMessage(response));
        }
    }
}
