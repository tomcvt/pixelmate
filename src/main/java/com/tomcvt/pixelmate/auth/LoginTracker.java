package com.tomcvt.pixelmate.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tomcvt.pixelmate.exceptions.IllegalUsageException;



@Service
public class LoginTracker {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginTracker.class);
    private Map<String, LoginCounter> failedLoginAttempts = new ConcurrentHashMap<>();
    private final long timeWindowMillis = 15 * 60 * 1000; // 15 minutes

    public void recordFailedLogin(String username) throws IllegalUsageException {
        failedLoginAttempts.compute(username, (key, counter) -> {
            if (counter == null) {
                return new LoginCounter();
            } else {
                long now = System.currentTimeMillis();
                // Reset counter if more than 15 minutes have passed since last reset
                if (now - counter.getLastReset() > timeWindowMillis) { // 15 minutes
                    counter.reset();
                } else {
                    counter.increment();
                }
                return counter;
            }
        });

        int attempts = failedLoginAttempts.get(username).getCount();
        //TODO make configurable
        if (attempts > 5) {
            failedLoginAttempts.get(username).reset();
            throw new IllegalUsageException("Too many failed login attempts. Please try again later in 15 minutes or from another ip.");
        }
        log.info("Recorded failed login for user {}: {} attempts", username, failedLoginAttempts.get(username));
    }

    @Scheduled(fixedRate = 30 * 60 * 1000) // Every 30 minutes
    public void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        failedLoginAttempts.entrySet().removeIf(entry -> 
            now - entry.getValue().getLastReset() > timeWindowMillis // 15 minutes
        );
        log.info("Cleaned up old login attempt entries");
    }
}
