package com.app.listeners;

import com.app.dto.PresenceMessage;
import com.app.model.User;
import com.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    private static final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = accessor.getUser();
        if (userPrincipal != null) {
            String googleId = userPrincipal.getName();
            connectedUsers.add(googleId);

            // Lookup display name
            String displayName = userService.getDisplayNameByGoogleId(googleId);

            // Broadcast JOIN event
            PresenceMessage presence = new PresenceMessage(displayName, "JOIN");
            messagingTemplate.convertAndSend("/topic/presence", presence);

            System.out.println("[WebSocket] User connected: " + displayName);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = accessor.getUser();
        if (userPrincipal != null) {
            String googleId = userPrincipal.getName();
            connectedUsers.remove(googleId);

            // Lookup display name
            String displayName = userService.getDisplayNameByGoogleId(googleId);

            // Broadcast LEAVE event
            PresenceMessage presence = new PresenceMessage(displayName, "LEAVE");
            messagingTemplate.convertAndSend("/topic/presence", presence);

            System.out.println("[WebSocket] User disconnected: " + displayName);
        }
    }

    public Set<String> getConnectedUsers() {
        return connectedUsers;
    }
}
