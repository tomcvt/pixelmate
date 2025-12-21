package com.tomcvt.pixelmate.auth;

public class LoginCounter {
    int count;
    long lastReset;

    public LoginCounter() {
        this.count = 1;
        this.lastReset = System.currentTimeMillis();
    }

    public int getCount() {
        return count;
    }

    public void increment() {
        this.count++;
    }

    public long getLastReset() {
        return lastReset;
    }

    public void reset() {
        this.count = 1;
        this.lastReset = System.currentTimeMillis();
    }

}
