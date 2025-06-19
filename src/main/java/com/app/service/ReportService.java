package com.app.service;

import com.app.model.Report;
import com.app.model.User;
import com.app.model.Message;
import com.app.model.Chatroom;
import com.app.repo.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    @Transactional
    public Optional<Report> submitMessageReport(User reporter, Message message, String reason) {
        if (!isUserInChatroom(reporter, message.getChatroom())) {
            return Optional.empty();
        }

        if (reportRepository.existsByReporterAndReportedMessage(reporter, message)) {
            return Optional.empty();
        }

        Report report = Report.builder()
                .reporter(reporter)
                .reportedMessage(message)
                .reportedUser(message.getSender())
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();

        return Optional.of(reportRepository.save(report));
    }

    private boolean isUserInChatroom(User user, Chatroom chatroom) {
        return chatroom.getMembers().contains(user);
    }

    public boolean hasAlreadyReported(User reporter, Message message) {
        return reportRepository.existsByReporterAndReportedMessage(reporter, message);
    }
}
