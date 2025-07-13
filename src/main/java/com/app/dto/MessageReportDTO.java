package com.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageReportDTO {
    private Long id;
    private String content;
    private String senderUsername;
    private String bannedUntil;
    private List<ReportDTO> reports;
    private String lastUpdated;
    private Long fileId;
    private String fileName;
    private String fileMimeType;
}