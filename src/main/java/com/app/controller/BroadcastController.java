package com.app.controller;

import com.app.model.User;
import com.app.service.BroadcastService;
import com.app.service.CurrentUserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/broadcast")
public class BroadcastController {

    private final BroadcastService broadcastService;
    private final CurrentUserService currentUserService;

    public BroadcastController(BroadcastService broadcastService, CurrentUserService currentUserService) {
        this.broadcastService = broadcastService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/manage")
    public String manageBroadcasts(Model model) {
        User admin = currentUserService.getCurrentAppUser();
        model.addAttribute("broadcasts", broadcastService.getActiveMessagesByAdmin(admin));
        return "broadcast-manage";
    }

    @PostMapping("/create")
    public String create(@RequestParam @NotBlank String content,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiresAt) {
        broadcastService.create(currentUserService.getCurrentAppUser(), content, expiresAt);
        return "redirect:/admin/broadcast/manage";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam Long id,
                       @RequestParam String content) {
        broadcastService.updateContent(currentUserService.getCurrentAppUser(), id, content);
        return "redirect:/admin/broadcast/manage";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        broadcastService.delete(currentUserService.getCurrentAppUser(), id);
        return "redirect:/admin/broadcast/manage";
    }
}