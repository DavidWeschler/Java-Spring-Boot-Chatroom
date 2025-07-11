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

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Registers a user via traditional signup (e.g., with password).
     */
    public User register(User user) {
        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    /**
     * Logs in or registers a user from Google OAuth login.
     * Uses googleId (unique) to determine identity.
     */
    public User findOrCreateGoogleUser(String googleId, String email, String displayName, String avatarId) {
        return userRepository.findByGoogleId(googleId).orElseGet(() -> {
            User user = new User();
            user.setGoogleId(googleId);
            user.setEmail(email);
            user.setUsername(displayName); // Not unique!
            user.setAvatarId(avatarId);
            user.setRole("USER");
            user.setCreatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public String getDisplayNameByGoogleId(String googleId) {
        return findByGoogleId(googleId)
                .map(User::getUsername) // or getDisplayName if you rename the field
                .orElse("Unknown User");
    }

    public void banUserByMessageId(Long messageId, String duration) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        User user = message.getSender();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime requestedUntil = switch (duration) {
            case "24h" -> now.plusHours(24);
            case "1w" -> now.plusDays(7);
            default -> now.plusYears(100); // ~forever
        };

        // Only apply ban if the new one is longer
        if (user.getBannedUntil() == null || user.getBannedUntil().isBefore(requestedUntil)) {
            user.setBannedUntil(requestedUntil);
            userRepository.save(user);
        }

        // Mark all PENDING reports for this message as ACTION_TAKEN
        List<Report> relatedReports = reportRepository.findByReportedMessageAndStatus(message, ReportStatus.PENDING);
        for (Report report : relatedReports) {
            report.setStatus(ReportStatus.ACTION_TAKEN);
        }
        reportRepository.saveAll(relatedReports);
    }

}

