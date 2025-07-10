package com.app.controller;

import com.app.model.Message;
import com.app.model.Report;
import com.app.model.ReportStatus;
import com.app.model.User;
import com.app.repo.MessageRepository;
import com.app.repo.ReportRepository;
import com.app.repo.UserRepository;
import com.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        System.out.println("entering adminPanel");

        // Get all distinct reported messages with active (non-dismissed) reports
        List<Message> reportedMessages = reportRepository.findDistinctReportedMessagesWithActiveReports();

        // For each message, load only its active reports and attach them to the message
        for (Message msg : reportedMessages) {
            List<Report> activeReports = reportRepository.findByReportedMessageAndStatusNot(msg, ReportStatus.DISMISSED);
            msg.setReports(activeReports);
        }

        model.addAttribute("reportedMessages", reportedMessages);
        return "admin-panel";
    }

    @PostMapping("/block-user/{userId}")
    public String blockUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole("BLOCKED");
        userRepository.save(user);
        return "redirect:/admin/panel";
    }

    @PostMapping("/ban-user/{userId}")
    public String banUser(@PathVariable Long userId, @RequestParam String duration) {
        userService.banUser(userId, duration);
        return "redirect:/admin/panel";
    }

    @PostMapping("/dismiss-report/{reportId}")
    public String dismissReport(@PathVariable Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow();
        report.setStatus(ReportStatus.DISMISSED);
        reportRepository.save(report);
        return "redirect:/admin/panel";
    }

    @PostMapping("/dismiss-message-reports/{messageId}")
    public String dismissAllReportsOnMessage(@PathVariable Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        List<Report> reports = reportRepository.findByReportedMessageAndStatusNot(message, ReportStatus.DISMISSED);

        for (Report r : reports) {
            r.setStatus(ReportStatus.DISMISSED);
        }

        reportRepository.saveAll(reports);  // batch update
        return "redirect:/admin/panel";
    }
}