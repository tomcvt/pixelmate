package com.tomcvt.pixelmate.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tomcvt.pixelmate.exceptions.IllegalUsageException;

@Service
public class IpRegistry {
    private final Map<String, RequestCounter> ipRequestCounters = new ConcurrentHashMap<>();
    private final int banThreshold;
    private final int hourlyLimit;

    public IpRegistry(@Value("${pixelmate.network.ban-threshold-minute}") int banThreshold,
                      @Value("${pixelmate.network.requests-per-hour}") int hourlyLimit) {
        this.banThreshold = banThreshold;
        this.hourlyLimit = hourlyLimit;
    }

    public boolean incrementAndCheckIfAllowed(String ip) throws IllegalUsageException {
        long currentTimeMillis = System.currentTimeMillis();
        ipRequestCounters.compute(ip, (key, counter) -> {
            if (counter == null) {
                return new RequestCounter();
            } else {
                if (currentTimeMillis - counter.getBanWindowStartMillis() > 60 * 1000) {
                    counter.resetBanCounter();
                } else {
                    counter.incrementBanCounter();
                }
                if (currentTimeMillis - counter.getHourWindowStartMillis() > 60 * 60 * 1000) {
                    counter.resetHourCounter();
                } else {
                    counter.incrementHourCounter();
                }
                return counter;
            }
        });
        
        RequestCounter counter = ipRequestCounters.get(ip);
        if (counter.getBanCounter() > banThreshold) {
            throw new IllegalUsageException("IP " + ip + " is temporarily banned due to excessive requests.");
        }

        if (counter.getHourCounter() > hourlyLimit) {
            return false;
        }
        return true;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void resetHourlyCounters() {
        long currentTimeMillis = System.currentTimeMillis();
        ipRequestCounters.entrySet().removeIf(entry -> 
            currentTimeMillis - entry.getValue().getHourWindowStartMillis() > 60 * 60 * 1000
        );
    }

}
