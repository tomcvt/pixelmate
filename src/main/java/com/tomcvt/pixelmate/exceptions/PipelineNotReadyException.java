package com.tomcvt.pixelmate.exceptions;

public class PipelineNotReadyException extends RuntimeException {
    public PipelineNotReadyException(String message) {
        super(message);
    }
}
