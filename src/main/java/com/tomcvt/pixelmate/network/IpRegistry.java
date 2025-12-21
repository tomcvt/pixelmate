package com.tomcvt.pixelmate.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IpRegistry {
    private final Map<String, RequestCounter> ipRequestCounters = new ConcurrentHashMap<>();
    private final int banThreshold = 100;
    private final int hourlyLimit = 1000;

    public IpRegistry() {
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
            return false;
        }
    }

}
