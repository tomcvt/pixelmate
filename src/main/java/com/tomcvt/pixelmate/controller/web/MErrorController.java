package com.tomcvt.pixelmate.controller.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tomcvt.pixelmate.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class MErrorController implements ErrorController {
    
    @RequestMapping(value = "/error", produces = "application/json")
    public ResponseEntity<ErrorResponse> returnErrorMsg(HttpServletRequest request, HttpServletResponse response) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        statusCode = response.getStatus();
        String message = (String) request.getAttribute("javax.servlet.error.message");
        if (message == null || message.isEmpty()) {
            message = "N/A";
        }
        String error;
        try {
            error = HttpStatus.valueOf(statusCode).name();
        } catch (Exception e) {
            error = "UNKNOWN";
        }
        switch (statusCode) {
            case 404:
                message = "The requested resource was not found.";
                break;
            case 403:
                error = "FORBIDDEN";
                message = "You do not have permission.";
                break;
            case 500:
                message = "An internal server error occurred.";
                break;
            // Add more cases as needed
            default:
                break;
        }
        ErrorResponse errorResponse = new ErrorResponse(error, message);
        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
