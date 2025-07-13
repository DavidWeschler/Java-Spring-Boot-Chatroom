package com.app.controller;

import com.app.dto.ChatMessageDTO;
import com.app.listeners.WebSocketEventListener;
import com.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    @MessageMapping("/chat")
    public void send(ChatMessageDTO message) {
        ChatMessageDTO updated = messageService.sendChatMessage(message);
        messagingTemplate.convertAndSend("/topic/messages/" + updated.getChatroomId(), updated);
    }

    @MessageMapping("/join")
    public void handleJoin(Map<String, Long> payload, StompHeaderAccessor accessor) {
        Long chatroomId = payload.get("chatroomId");
        Principal user = accessor.getUser();
        String sessionId = accessor.getSessionId();

        if (user != null && chatroomId != null) {
            webSocketEventListener.addUserToChatroom(user.getName(), chatroomId, sessionId);
        }
    }
}
