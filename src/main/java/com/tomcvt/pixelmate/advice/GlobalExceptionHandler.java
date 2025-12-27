package com.tomcvt.pixelmate.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.tomcvt.pixelmate.dto.ErrorResponse;
import com.tomcvt.pixelmate.exceptions.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse("UNEXPECTED_ERROR", "An unexpected error occurred."));
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
    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<ErrorResponse> handleRequestRejectedException(RequestRejectedException ex) {
        log.error("Request rejected exception: ", ex.getMessage());
        return ResponseEntity
                .status(400)
                .body(new ErrorResponse("REQUEST_REJECTED", "The request was rejected by the server."));
    }
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
        log.error("Multipart exception: ", ex.getMessage());
        return ResponseEntity
                .status(400)
                .body(new ErrorResponse("MULTIPART_ERROR", "There was an error processing the multipart request."));
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("RequestMethodNotSupportedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            new ErrorResponse("METHOD_NOT_ALLOWED", "The request method is not supported for this endpoint.")
        );
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("AuthorizationDeniedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            new ErrorResponse("FORBIDDEN", "You do not have permission to access this resource.")
        );
    }
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException ex) {
        log.error("FileUploadException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("FILE_UPLOAD_ERROR", ex.getMessage())
        );
    }
    @ExceptionHandler(PipelineNotReadyException.class)
    public ResponseEntity<ErrorResponse> handlePipelineNotReadyException(PipelineNotReadyException ex) {
        log.error("PipelineNotReadyException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("PIPELINE_NOT_READY", ex.getMessage())
        );
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
