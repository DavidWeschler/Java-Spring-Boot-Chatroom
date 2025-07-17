package com.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * GlobalExceptionHandler handles exceptions thrown by the application.
 * It provides specific responses for 404 (Not Found) and 403 (Forbidden) errors,
 * as well as a generic handler for all other exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns error.html with 404 status code.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        return "error";
    }

    /**
     * Handles ForbiddenException and returns error.html with 403 status code.
     */
    @ExceptionHandler(ForbiddenException.class)
    public String handleForbidden(ForbiddenException ex, Model model) {
        model.addAttribute("statusCode", HttpStatus.FORBIDDEN.value());
        return "error";
    }

    /**
     * Handles all other exceptions and returns error.html with 500 status code.
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "error";
    }
}
