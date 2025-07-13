package com.app.controller;

import com.app.dto.BannedUserDTO;
import com.app.dto.MessageReportDTO;
import com.app.model.Message;
import com.app.model.User;
import com.app.repo.UserRepository;
import com.app.service.AdminService;
import com.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        model.addAttribute("reportedMessages", adminService.getReportedMessagesWithReportsAttached());
        adminService.cleanupExpiredBans();
        return "admin-panel";
    }

    @PostMapping("/ban-user/{msgId}")
    public String banUser(@PathVariable Long msgId, @RequestParam String duration) {
        userService.banUserByMessageId(msgId, duration);
        return "redirect:/admin/panel";
    }

    @PostMapping("/dismiss-message-reports/{messageId}")
    public String dismissAllReportsOnMessage(@PathVariable Long messageId) {
        adminService.dismissReportsForMessage(messageId);
        return "redirect:/admin/panel";
    }

    @GetMapping("/panel/reports")
    @ResponseBody
    public List<MessageReportDTO> getLatestReports(@RequestParam(required = false) String since) {
        return adminService.getLatestReportsSince(since);
    }

    @GetMapping("/panel/banned-users")
    @ResponseBody
    public List<BannedUserDTO> getBannedUsers() {
        LocalDateTime now = LocalDateTime.now();
        return userRepository.findByBannedUntilIsNotNullAndBannedUntilAfter(now)
                .stream()
                .map(user -> new BannedUserDTO(user.getId(), user.getUsername(), user.getBannedUntil()))
                .collect(Collectors.toList());
    }
}
