package com.tomcvt.pixelmate.controller.api;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.pixelmate.dto.TextResponse;
import com.tomcvt.pixelmate.network.BanRegistry;

@RestController
@RequestMapping("/api/banning")
@PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
public class BanningApiController {
    private final BanRegistry banRegistry;

    public BanningApiController(BanRegistry banRegistry) {
        this.banRegistry = banRegistry;
    }

    @GetMapping("/banned-ips")
    public ResponseEntity<Map<String,String>> getBannedIPs() {
        Map<String, Long> bannedIPsWithExpiry = banRegistry.getBannedIPs();
        Map<String, String> bannedIPsFormatted = bannedIPsWithExpiry.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Instant.ofEpochMilli(e.getValue()).toString()
            ));
        return ResponseEntity.ok(bannedIPsFormatted);
    }
    //TODO add frontend page to manage banned IPs
    @PostMapping("/ban-ip")
    public ResponseEntity<?> banIP(@RequestParam String ipAddress, @RequestParam Long durationMinutes) {
        banRegistry.banIp(ipAddress, durationMinutes);
        return ResponseEntity.ok(new TextResponse("IP " + ipAddress + " has been banned for " + durationMinutes + " minutes."));
    }
    @PostMapping("/unban-ip")
    public ResponseEntity<?> unbanIP(@RequestParam String ipAddress) {
        banRegistry.unbanIp(ipAddress);
        return ResponseEntity.ok(new TextResponse("IP " + ipAddress + " has been unbanned."));
    }
}