package com.app.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied() {
        System.out.println("Access denied");
        return "redirect:/chatrooms"; // or return "error/403";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        System.out.println("File upload too large: " + exc.getMessage());
        redirectAttributes.addFlashAttribute("error", "File is too large. Maximum size allowed is 16MB.");
        // Redirect to chatrooms page, or consider redirecting to the chatroom page where upload was attempted
        return "redirect:/chatrooms";
    }
}
