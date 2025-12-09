package com.tomcvt.pixelmate.service;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class SessionCleanupService implements HttpSessionListener {
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        // Perform cleanup actions here, such as deleting temporary files
        String sessionId = session.getId();
        // Example: Delete files in the cache directory associated with this sessionId
        
        
    }
    
}
