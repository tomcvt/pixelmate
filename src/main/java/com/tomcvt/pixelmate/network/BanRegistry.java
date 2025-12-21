package com.tomcvt.pixelmate.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for managing IP bans within the application.
 * <p>
 * The {@code BanRegistry} allows banning IP addresses for a configurable default duration
 * or for a custom duration. It maintains an in-memory registry of banned IPs and their
 * corresponding ban expiry times. The service provides methods to ban IPs, check if an IP
 * is currently banned, and automatically removes expired bans upon access.
 * </p>
 * <p>
 * Thread-safe operations are ensured via the use of a {@link java.util.concurrent.ConcurrentHashMap}.
 * </p>
 *
 * <h2>Configuration</h2>
 * <ul>
 *   <li>{@code pixelmate.network.default-ban-duration-minute} - The default ban duration in minutes.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>
 * {@code
 * banRegistry.banIp("192.168.1.1"); // Ban with default duration
 * banRegistry.banIp("192.168.1.2", 30); // Ban for 30 minutes
 * boolean banned = banRegistry.isBanned("192.168.1.1");
 * }
 * </pre>
 * @author Tomasz Wiśniewski
 * @since 1.0
 */
@Service
public class BanRegistry {
    private final Map<String, Long> bannedIps = new ConcurrentHashMap<>();
    private final long defaultBanDurationMillis;

    public BanRegistry(@Value("${pixelmate.network.default-ban-duration-minute}") long defaultBanDurationMinutes) {
        this.defaultBanDurationMillis = defaultBanDurationMinutes * 60 * 1000;
    }

    public void banIp(String ip) {
        long banExpiryTime = System.currentTimeMillis() + defaultBanDurationMillis;
        bannedIps.put(ip, banExpiryTime);
    }

    /**
     * Bans the specified IP address for a given duration.
     *
     * @param ip the IP address to ban
     * @param durationMinutes the duration of the ban in minutes
     */
    public void banIp(String ip, long durationMinutes) {
        long banExpiryTime = System.currentTimeMillis() + durationMinutes * 60 * 1000;
        bannedIps.put(ip, banExpiryTime);
    }

    
    /**
     * Checks if the specified IP address is currently banned.
     * <p>
     * This method retrieves the ban expiry time for the given IP address from the {@code bannedIps} map.
     * If the IP is not found, it is considered not banned. If the ban has expired, the IP is removed
     * from the ban list and considered not banned. Otherwise, the IP is still banned.
     *
     * @param ip the IP address to check
     * @return {@code true} if the IP is currently banned; {@code false} otherwise
     */
    public boolean isBanned(String ip) {
        Long expiryTime = bannedIps.get(ip);
        if (expiryTime == null) {
            return false;
        }
        if (System.currentTimeMillis() > expiryTime) {
            bannedIps.remove(ip);
            return false;
        }
        return true;
    }


    public void unbanIp(String ip) {
        bannedIps.remove(ip);
    }

    public Map<String, Long> getBannedIPs() {
        return bannedIps;
    }

}
