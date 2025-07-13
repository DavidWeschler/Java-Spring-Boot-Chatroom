package com.app.service;

import com.app.model.Message;
import com.app.model.Report;
import com.app.model.ReportStatus;
import com.app.model.User;
import com.app.repo.MessageRepository;
import com.app.repo.ReportRepository;
import com.app.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MessageRepository messageRepository;

    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public String getDisplayNameByGoogleId(String googleId) {
        return findByGoogleId(googleId)
                .map(User::getUsername)
                .orElse("Unknown User");
    }

    public void banUserByMessageId(Long messageId, String duration) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        User user = message.getSender();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime requestedUntil = switch (duration) {
            case "24h" -> now.plusHours(24);
            case "1w" -> now.plusDays(7);
            default -> now.plusYears(100);
        };

        if (user.getBannedUntil() == null || user.getBannedUntil().isBefore(requestedUntil)) {
            user.setBannedUntil(requestedUntil);
            userRepository.save(user);
        }

        List<Report> relatedReports = reportRepository.findByReportedMessageAndStatus(message, ReportStatus.PENDING);
        for (Report report : relatedReports) {
            report.setStatus(ReportStatus.ACTION_TAKEN);
            report.setUpdatedAt(LocalDateTime.now());
        }
        reportRepository.saveAll(relatedReports);
    }
}
