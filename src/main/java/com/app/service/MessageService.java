package com.app.service;

import com.app.model.Chatroom;
import com.app.model.Message;
import com.app.model.User;
import com.app.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getMessagesForChatroom(Chatroom chatroom) {
        return messageRepository.findByChatroomOrderByTimestampAsc(chatroom);
    }

    public void sendMessageToChatroom(String content, Chatroom chatroom, User sender) {
        Message message = new Message();
        message.setSender(sender);
        message.setChatroom(chatroom);
        message.setContent(content);
        message.setTimestamp(java.time.LocalDateTime.now());
        messageRepository.save(message);
    }
}

