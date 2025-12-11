package com.tomcvt.pixelmate.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Service
public class SessionCleanupService implements HttpSessionListener {
    private static final Logger log = LoggerFactory.getLogger(SessionCleanupService.class);
    private final String cacheDir;

    public SessionCleanupService(@Value("${pixelmate.cache-dir}") String cacheDir) {
        this.cacheDir = cacheDir;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String sessionId = session.getId();
        log.info("Session destroyed: " + sessionId + ". Cleanup actions performed.");
        File sessionCacheDir = new File(cacheDir, sessionId);
        if (sessionCacheDir.exists() && sessionCacheDir.isDirectory()) {
            deleteDirectoryRecursively(sessionCacheDir);
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
