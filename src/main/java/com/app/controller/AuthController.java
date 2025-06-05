package com.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";  // Thymeleaf template login.html
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new RegisterForm()); // create this class for form binding
        return "register";  // Thymeleaf template register.html
    }
//
//    @PostMapping("/register")
//    public String registerUser(@Valid @ModelAttribute("user") RegisterForm form, BindingResult bindingResult, Model model) {
//        if (bindingResult.hasErrors()) {
//            return "register";
//        }
//
//        if (!form.getPassword().equals(form.getConfirmPassword())) {
//            model.addAttribute("registrationError", "Passwords do not match");
//            return "register";
//        }
//
//        // Here call your UserService to create user (Dev A's task)
//        // For now, you can assume a method like userService.registerUser(form)
//        // userService.registerUser(form);
//
//        // If registration succeeds:
//        return "redirect:/login?registered";  // Show a message on login page if needed
//    }
}
