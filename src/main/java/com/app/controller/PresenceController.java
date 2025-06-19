package com.app.controller;

import com.app.listeners.WebSocketEventListener;
import com.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/presence")
public class PresenceController {

    @Autowired
    private WebSocketEventListener listener;

    @Autowired
    private UserService userService;

    @GetMapping("/online/chatroom/{id}")
    public Set<String> getOnlineUsers(@PathVariable Long id) {
        return listener.getConnectedUsers(id).stream()
                .map(userService::getDisplayNameByGoogleId)
                .collect(Collectors.toSet());
    }
}
