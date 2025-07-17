package com.app.controller;

import com.app.exception.ResourceNotFoundException;
import com.app.model.BroadcastMessage;
import com.app.model.Chatroom;
import com.app.model.User;
import com.app.repo.ChatroomRepository;
import com.app.repo.UserRepository;
import com.app.service.BroadcastService;
import com.app.service.CurrentUserService;
import com.app.session.UserSessionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HomeController handles requests for the home page and logout confirmation.
 * It retrieves the current user's information, recent chatrooms, and active broadcast messages.
 */
@Controller
public class HomeController {

    /**
     * Service to retrieve the current user's information.
     */
    @Autowired
    private CurrentUserService currentUserService;

    /**
     * Session bean to manage user session data.
     */
    @Autowired
    private UserSessionBean userSession;

    /**
     * Repository to access chatroom data.
     */
    @Autowired
    private ChatroomRepository chatroomRepository;

    /**
     * Service to handle broadcast messages.
     */
    @Autowired
    private BroadcastService broadcastService;

    /**
     * Repository to access user data.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Displays the home page with the current user's information, recent chatrooms,
     * and active broadcast messages.
     *
     * @param model the model to add attributes for the view
     * @return the name of the home view
     */
    @GetMapping("/home")
    public String home(Model model) {
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUser.getAttribute("email")));

        model.addAttribute("name", currentUser.getAttribute("name"));
        model.addAttribute("email", currentUser.getAttribute("email"));

        List<Long> recentIds = userSession.getRecentChatrooms();
        List<Chatroom> recentChatrooms = chatroomRepository.findAllById(recentIds);
        Map<Long, String> displayNames = recentChatrooms.stream().collect(Collectors.toMap(Chatroom::getId,c -> c.getDisplayName(user)));

        model.addAttribute("displayNames", displayNames);
        model.addAttribute("recentChatrooms", recentChatrooms);

        List<BroadcastMessage> broadcasts = broadcastService.getActiveMessages();
        model.addAttribute("broadcasts", broadcasts);

        return "home";
    }

    /**
     * Displays the logout confirmation page.
     *
     * @return the name of the logout confirmation view
     */
    @GetMapping("/logout")
    public String confirmLogout() {
        return "logout-confirm";
    }
}
