package com.tomcvt.pixelmate.dto;

public record PipelineMetadataDto(
    String sessionId,
    String estimatedMemoryUsageMB,
    String creationTime
) {
    
}
