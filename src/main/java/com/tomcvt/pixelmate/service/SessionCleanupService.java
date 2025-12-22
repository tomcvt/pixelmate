package com.tomcvt.pixelmate.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tomcvt.pixelmate.registry.PipelineInfoRegistry;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Service
public class SessionCleanupService implements HttpSessionListener {
    private static final Logger log = LoggerFactory.getLogger(SessionCleanupService.class);
    private final PipelineInfoRegistry pipelineInfoRegistry;
    private final String cacheDir;

    public SessionCleanupService(@Value("${pixelmate.cache-dir}") String cacheDir, 
            PipelineInfoRegistry pipelineInfoRegistry) {
        this.cacheDir = cacheDir;
        this.pipelineInfoRegistry = pipelineInfoRegistry;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String sessionId = session.getId();
        
        File sessionCacheDir = new File(cacheDir, sessionId);
        if (sessionCacheDir.exists() && sessionCacheDir.isDirectory()) {
            deleteDirectoryRecursively(sessionCacheDir);
        }
        pipelineInfoRegistry.removePipelineInfo(sessionId);
        log.info("Session destroyed: " + sessionId + ". Cleanup actions performed.");
        var remainingPipelines = pipelineInfoRegistry.getRegisteredPipelinesCount();
        var memoryUsageInfo = pipelineInfoRegistry.getMemoryUsageInfo();
        log.info("Remaining active pipelines: {}. Memory usage: {}", remainingPipelines, memoryUsageInfo);
    }

    public void clearSessionDiskCache(String sessionId) {
        File sessionCacheDir = new File(cacheDir, sessionId);
        if (sessionCacheDir.exists() && sessionCacheDir.isDirectory()) {
            deleteDirectoryRecursively(sessionCacheDir);
            log.info("Cleared disk cache for session: " + sessionId);
        } else {
            log.info("No disk cache found for session: " + sessionId);
        }
    }

    private void deleteDirectoryRecursively(File dir) {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryRecursively(file);
            }
        }
        dir.delete();
    }
    
}
