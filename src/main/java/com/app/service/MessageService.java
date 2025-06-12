package com.app.service;

import com.app.model.Chatroom;
import com.app.model.Message;
import com.app.model.File;
import com.app.model.User;
import com.app.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getMessagesForChatroom(Chatroom chatroom) {
        return messageRepository.findByChatroomOrderByTimestampAsc(chatroom);
    }

    public void sendMessageToChatroom(String content, Chatroom chatroom, User sender, File file) {
        Message message = new Message();
        message.setContent(content);
        message.setChatroom(chatroom);
        message.setSender(sender);
        message.setTimestamp(LocalDateTime.now());
        message.setFile(file);
        messageRepository.save(message);
    }
}

