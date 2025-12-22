package com.tomcvt.pixelmate.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tomcvt.pixelmate.pipeline.PipelineMetadata;

@Service
public class PipelineInfoRegistry {
    private final Map<String, PipelineMetadata> pipelineInfoMap = new ConcurrentHashMap<>();
    private final long maxMemoryUsageBytes;
    private final int maxActivePipelines;

    public PipelineInfoRegistry(@Value("${pixelmate.limits.memory-usage-mb}") long maxMemoryUsageMb,
            @Value("${pixelmate.limits.active-pipelines}") int maxActivePipelines) {
        this.maxMemoryUsageBytes = maxMemoryUsageMb * 1024 * 1024;
        this.maxActivePipelines = maxActivePipelines;
    }


    public void registerPipelineInfo(String sessionId, PipelineMetadata metadata) {
        if (pipelineInfoMap.size() >= maxActivePipelines && !pipelineInfoMap.containsKey(sessionId)) {
            throw new IllegalStateException("Cannot register new pipeline: maximum number of active pipelines reached (" + maxActivePipelines + ")");
        }
        pipelineInfoMap.put(sessionId, metadata);
    }

    public PipelineMetadata updatePipelineInfo(String sessionId, PipelineMetadata metadata) {
        if (!pipelineInfoMap.containsKey(sessionId)) {
            throw new IllegalStateException("Cannot update pipeline info: session ID not found");
        }
        return pipelineInfoMap.put(sessionId, metadata);
    }

    public PipelineMetadata getPipelineInfo(String sessionId) {
        return pipelineInfoMap.get(sessionId);
    }

    public void removePipelineInfo(String sessionId) {
        pipelineInfoMap.remove(sessionId);
    }
    public long getRegisteredPipelinesCount() {
        return pipelineInfoMap.size();
    }

    public long getTotalEstimatedMemoryUsageBytes() {
        return pipelineInfoMap.values().stream()
                .mapToLong(PipelineMetadata::estimatedMemoryUsageBytes)
                .sum();
    }

    public long getMaxMemoryUsageBytes() {
        return maxMemoryUsageBytes;
    }

    public String getMemoryUsageInfo() {
        long totalUsage = getTotalEstimatedMemoryUsageBytes();
        return String.format("Current memory usage: %.2f MB / %.2f MB",
                totalUsage / (1024.0 * 1024.0),
                maxMemoryUsageBytes / (1024.0 * 1024.0));
    }

    public Map<String, PipelineMetadata> getAllPipelineInfo() {
        return pipelineInfoMap;
    }

}
