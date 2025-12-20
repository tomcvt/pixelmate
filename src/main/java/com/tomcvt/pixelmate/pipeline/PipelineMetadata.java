package com.tomcvt.pixelmate.pipeline;

import java.time.Instant;

public record PipelineMetadata(
    String sessionId,
    long estimatedMemoryUsageBytes,
    Instant creationTime
) {
    
}
