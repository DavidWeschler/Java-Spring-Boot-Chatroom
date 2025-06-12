package com.app.service;

import com.app.model.Chatroom;
import com.app.model.Message;
import com.app.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getMessagesForChatroom(Chatroom chatroom) {
        return messageRepository.findByChatroomOrderByTimestampAsc(chatroom);
    }
}
