package com.app.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String from;
    private String text;
    private String time;
    private Long chatroomId;
    private Long fromId;
    private Long id;
    private Long fileId;
    private String filename;
    private String mimeType;
}
