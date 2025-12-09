package com.tomcvt.pixelmate.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class SessionsRegistry {
    private final Map<String, UUID> sessions = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, UUID userId) {
        sessions.put(sessionId, userId);
    }
}
