package com.tomcvt.pixelmate.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.tomcvt.pixelmate.dto.ErrorResponse;
import com.tomcvt.pixelmate.exceptions.DebugException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse("UNEXPECTED_ERROR", ex.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception: ", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("INVALID_INPUT", ex.getMessage()));
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("No resource found exception: ", ex.getMessage());
        return ResponseEntity
                .status(404)
                .body(new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage()));
    }
    @Profile("dev")
    @ExceptionHandler(DebugException.class)
    public ResponseEntity<ErrorResponse> handleDebugException(DebugException ex) {
        log.debug("Debug exception: ", ex);
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse("DEBUG_ERROR", ex.toString()));
    }
}
