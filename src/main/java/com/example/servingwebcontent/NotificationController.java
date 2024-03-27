package com.example.servingwebcontent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //@Scheduled(fixedRate = 60000) // Execute every minute
    @Scheduled(fixedRate = 30000)
    public void sendNotifications() {
        // Get current date
        String currentDate = getCurrentDateAsString();

        // Send current date to clients subscribed to /topic/notifications
        messagingTemplate.convertAndSend("/topic/notifications", currentDate);
    }

    private String getCurrentDateAsString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    //@Scheduled(fixedRate = 30000)
    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public String getNotifications(@Headers Map<String, Object> headers) {
        String jwtToken = extractTokenFromHeaders(headers);
        String userName = getUserNameFromToken(jwtToken);

        String currentDate = getCurrentDateAsString();
        return jwtToken;
    }

    private String getUserNameFromToken(String jwtToken) {
        // decode JWT and get userName
        return "simple_user";
    }

    private String extractTokenFromHeaders(Map<String, Object> headers) {
        // Retrieve JWT token from headers
        LinkedMultiValueMap<String, String> nativeHeaders = (LinkedMultiValueMap<String, String>) headers.get("nativeHeaders");
        String authHeaders = nativeHeaders.getFirst("Authorization");
        if (authHeaders != null && authHeaders.startsWith("Bearer ")) {
            return authHeaders.substring(7); // Remove 'Bearer ' prefix
        }
        return null; // Token not found or invalid format
    }
}
