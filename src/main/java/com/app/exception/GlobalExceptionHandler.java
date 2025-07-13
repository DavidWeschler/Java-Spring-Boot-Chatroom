//package com.app.exception;
//
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.multipart.MaxUploadSizeExceededException;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public String handleAccessDenied() {
//        return "redirect:/chatrooms";
//    }
//
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute("error", "File is too large");
//        // Redirect to chatrooms page, or consider redirecting to the chatroom page where upload was attempted
//        return "redirect:/chatrooms";
//    }
//}
