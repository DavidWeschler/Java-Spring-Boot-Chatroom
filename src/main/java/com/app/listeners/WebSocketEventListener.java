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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    private final Map<Long, Set<String>> chatroomUsers = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionChatroomMap = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    private Long extractChatroomId(StompHeaderAccessor accessor) {
        String idHeader = accessor.getFirstNativeHeader("chatroomId");
        return (idHeader != null) ? Long.parseLong(idHeader) : null;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        Long chatroomId = extractChatroomId(accessor);

        System.out.println("new user connected: " + (user != null ? user.getName() : "anonymous") +
                           " to chatroom: " + chatroomId);

        if (user != null && chatroomId != null) {
            String userId = user.getName();
            String sessionId = accessor.getSessionId();

            chatroomUsers.computeIfAbsent(chatroomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
            sessionChatroomMap.put(sessionId, chatroomId); // Map session to chatroom for disconnect handling

            String username = userService.getDisplayNameByGoogleId(userId);

            messagingTemplate.convertAndSend("/topic/presence/" + chatroomId,
                    new PresenceMessage(username, "JOIN"));

            System.out.println("[WebSocket] " + username + " joined chatroom " + chatroomId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        String sessionId = accessor.getSessionId();
        Long chatroomId = sessionChatroomMap.remove(sessionId);

        System.out.println("user disconnected: " + (user != null ? user.getName() : "anonymous") +
                           " from chatroom: " + chatroomId);

        if (user != null && chatroomId != null) {
            String userId = user.getName();
            Set<String> users = chatroomUsers.get(chatroomId);

            if (users != null) {
                users.remove(userId);
                if (users.isEmpty()) {
                    chatroomUsers.remove(chatroomId);
                }
            }

            String username = userService.getDisplayNameByGoogleId(userId);

            messagingTemplate.convertAndSend("/topic/presence/" + chatroomId,
                    new PresenceMessage(username, "LEAVE"));

            System.out.println("[WebSocket] " + username + " left chatroom " + chatroomId);
        }
    }

    public Set<String> getConnectedUsers(Long chatroomId) {
        System.out.println("[WebSocket] Fetching connected users for chatroom " + chatroomId);
        return chatroomUsers.getOrDefault(chatroomId, Collections.emptySet());
    }

    public void addUserToChatroom(String userId, Long chatroomId, String sessionId) {
        chatroomUsers.computeIfAbsent(chatroomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
        sessionChatroomMap.put(sessionId, chatroomId);

        String username = userService.getDisplayNameByGoogleId(userId);
        messagingTemplate.convertAndSend("/topic/presence/" + chatroomId,
                new PresenceMessage(username, "JOIN"));
    }

}
