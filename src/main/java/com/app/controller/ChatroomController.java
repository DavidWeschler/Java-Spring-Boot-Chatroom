package com.app.controller;

import com.app.model.Chatroom;
import com.app.model.ChatroomType;
import com.app.model.Message;
import com.app.model.User;
import com.app.projection.UserProjection;
import com.app.repo.MessageRepository;
import com.app.repo.UserRepository;
import com.app.service.ChatroomService;
import com.app.service.CurrentUserService;
import com.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/chatrooms")
public class ChatroomController {

    @Autowired
    private ChatroomService chatroomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    @PostMapping("/{chatroomId}/send-message")
    public String sendMessage(@PathVariable Long chatroomId,
                              @RequestParam("message") String content,
                              RedirectAttributes redirectAttributes) {

        if (content.length() > 255) {
            redirectAttributes.addFlashAttribute("error", "Message must be 255 characters or less.");
            return "redirect:/chatrooms/" + chatroomId + "/view-chatroom";
        }

        User user = chatroomService.requireMembershipOrThrow(chatroomId);
        Chatroom chatroom = chatroomService.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("Chatroom not found"));

        messageService.sendMessageToChatroom(content, chatroom, user);

        return "redirect:/chatrooms/" + chatroomId + "/view-chatroom";
    }



    @GetMapping("/conversations/start")
    public String showStartConversationPage(@RequestParam(value = "query", required = false) String query, Model model) {
        User currentUser = currentUserService.getCurrentAppUser();  // maybe move to an interceptor

        List<User> users = List.of();
        if (query != null && !query.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCase(query);
        }

        model.addAttribute("query", query);
        model.addAttribute("users", users);

        return "start-conversation";
    }

    @PostMapping("/conversations/start/{userId}")
    public String startConversation(@PathVariable String userId) {
        System.out.println("starting conversation with userId: " + userId);
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        if (user.getId().toString().equals(userId)) {
            System.out.println("error, cannot start conversation with self");
            return "redirect:/home"; // Cannot start a convo with self
        }

        Chatroom chat = chatroomService.findOrCreatePrivateChat(user.getId(), Long.parseLong(userId));
        return "redirect:/chatrooms/" + chat.getId() + "/view-chatroom";
    }

    @GetMapping("/{chatroomId}/search-members")
    public String searchMembers(@PathVariable Long chatroomId,
                                      @RequestParam(required = false) String query,
                                      Model model) {

        User user = chatroomService.requireMembershipOrThrow(chatroomId);
        Chatroom chatroom = chatroomService.findById(chatroomId).orElseThrow();
        List<User> members = chatroomService.getChatroomMembers(chatroomId);
        List<UserProjection> users = (query == null || query.isEmpty())
                ? List.of()
                : chatroomService.searchUsersNotInGroup(chatroomId, query);

        // prints the users found
        users.forEach(u -> System.out.println("User found: " + u.getUsername()));

        model.addAttribute("chatroom", chatroom);
        model.addAttribute("members", members);
        model.addAttribute("users", users);
        model.addAttribute("chatroomId", chatroomId);
        model.addAttribute("query", query);
        model.addAttribute("chatroomType", chatroomService.findById(chatroomId)
                .map(Chatroom::getType)
                .map(Enum::toString)
                .orElse("UNKNOWN"));
        System.out.println("now im supposed to show the chatroom manage page");
        model.addAttribute("editNameMode", false);
        return "chatroom-manage";
    }

    @GetMapping("/create-community")
    public String createCommunity() {
        return "chatroom-create-community";
    }

    @PostMapping("/create-community")
    public String createCommunity(@RequestParam String name,
                                  @RequestParam(required = false) boolean editableName) {
        System.out.println("creating community with name: " + name);
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        Chatroom chatroom = chatroomService.createCommunity(name, user);
        return "redirect:/chatrooms/" + chatroom.getId() + "/view-chatroom";
    }

    @GetMapping("/getCommunities")
    public String getCommunities(Model model) {
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        List<Chatroom> communities = chatroomService.findDiscoverableCommunities(user.getId());
        model.addAttribute("communities", communities);

        return "discover";
    }

    @GetMapping("/search-community")
    public String searchCommunity(@RequestParam String query, Model model) {
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        List<Chatroom> foundCommunities = chatroomService.searchCommunities(query, user.getId());
        model.addAttribute("foundCommunities", foundCommunities);
        model.addAttribute("query", query);

        return "discover";
    }

    //---------------------------------------------------------
    @GetMapping("")
    public String myChatrooms(Model model) {
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        List<Chatroom> myChats = chatroomService.findMyChatrooms(user.getId());
        model.addAttribute("chatrooms", myChats);

        return "chatrooms";
    }

    @GetMapping("/discover")
    public String discoverCommunities(Model model) {
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        List<Chatroom> communities = chatroomService.findDiscoverableCommunities(user.getId());
        model.addAttribute("communities", communities);

        return "discover";
    }

    @PostMapping("/join/{chatroomId}")
    public String joinCommunity(@PathVariable Long chatroomId) {
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        chatroomService.joinCommunity(chatroomId, user.getId());
        return "redirect:/chatrooms/discover";
    }

    @PostMapping("/leave/{chatroomId}")
    public String leaveChatroom(@PathVariable Long chatroomId) {
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        chatroomService.leaveChatroom(chatroomId, user.getId());
        return "redirect:/chatrooms";
    }

    @PostMapping("/{chatroomId}/edit")
    public String editChatroomName(@PathVariable Long chatroomId, @RequestParam String name, Model model) {
        User user = chatroomService.requireMembershipOrThrow(chatroomId);

        Chatroom chatroom = chatroomService.editChatroomName(chatroomId, user.getId(), name);
        model.addAttribute("chatroom", chatroom);
        return "redirect:/chatrooms/" + chatroomId + "/manage";
    }

    @PostMapping("/{chatroomId}/add-member/{userId}")
    public String addUserToGroup(@PathVariable Long chatroomId, @PathVariable Long userId, Model model) {
        chatroomService.requireMembershipOrThrow(chatroomId);
        Chatroom chatroom = chatroomService.findById(chatroomId).orElseThrow();
        chatroomService.addUserToGroup(chatroomId, userId);
        model.addAttribute("chatroom", chatroom);
        return "redirect:/chatrooms/" + chatroomId + "/manage";
    }

    @GetMapping("/create-group")
    public String createGroup() {
        return "chatroom-create-group";
    }

    @GetMapping("/create-conversation")
    public String createConversation() {
        return "start-conversation";
    }

    // this needs to be changed to be called "create-group"
    @PostMapping("/create")
    public String createGroup(@RequestParam String name,
                              @RequestParam(required = false) boolean editableName) {
        OAuth2User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getAttribute("email")).orElseThrow();

        Chatroom chatroom = chatroomService.createGroup(name, editableName, user);

        return "redirect:/chatrooms/" + chatroom.getId() + "/view-chatroom";
    }

    @GetMapping("/{chatroomId}/view-chatroom")
    public String viewChatroom(@PathVariable Long chatroomId, Model model) {

        chatroomService.requireMembershipOrThrow(chatroomId);

        Chatroom chatroom = chatroomService.findById(chatroomId).orElseThrow();

        List<Message> messages = messageRepository.findByChatroomOrderByTimestampAsc(chatroom);

        model.addAttribute("chatroomId", chatroomId);
        model.addAttribute("chatroomType", chatroom.getType().toString());
        model.addAttribute("messages", messages);

        return "view-chatroom";
    }

    @GetMapping("/{chatroomId}/manage")
    public String manageChatroom(@PathVariable Long chatroomId,
                                 @RequestParam(required = false) String query,
                                 @RequestParam(name = "editName", defaultValue = "false") String editNameParam,
                                 Model model) {

        boolean editNameMode = "true".equalsIgnoreCase(editNameParam);

        chatroomService.requireMembershipOrThrow(chatroomId);
        Chatroom chatroom = chatroomService.findById(chatroomId).orElseThrow();

        model.addAttribute("chatroomId", chatroomId);
        model.addAttribute("chatroomType", chatroom.getType().toString());

        // Chatroom members
        List<User> members = chatroomService.getChatroomMembers(chatroomId);
        model.addAttribute("members", members);

        // Search users not in group
        List<UserProjection> users = (query == null || query.isEmpty())
                ? List.of()
                : chatroomService.searchUsersNotInGroup(chatroomId, query);
        model.addAttribute("query", query);
        model.addAttribute("users", users);

        // Chatroom object + editNameMode flag
        model.addAttribute("chatroom", chatroom);
        model.addAttribute("editNameMode", editNameMode);
        System.out.println("DEBUG: editNameParam = " + editNameParam);
        return "chatroom-manage";
    }
}