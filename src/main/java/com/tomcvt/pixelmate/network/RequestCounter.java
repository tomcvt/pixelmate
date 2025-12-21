package com.tomcvt.pixelmate.network;

public class RequestCounter {
    private int banCounter;
    private int hourCounter;
    private long banWindowStartMillis;
    private long hourWindowStartMillis;

    public RequestCounter() {
        this.banCounter = 1;
        this.hourCounter = 1;
        this.banWindowStartMillis = System.currentTimeMillis();
        this.hourWindowStartMillis = this.banWindowStartMillis;
    }

    public int getBanCounter() {
        return banCounter;
    }

    public void incrementBanCounter() {
        this.banCounter++;
    }

    public int getHourCounter() {
        return hourCounter;
    }

    public void incrementHourCounter() {
        this.hourCounter++;
    }

    public long getBanWindowStartMillis() {
        return banWindowStartMillis;
    }

    public long getHourWindowStartMillis() {
        return hourWindowStartMillis;
    }

    public void resetBanCounter() {
        this.banCounter = 1;
        this.banWindowStartMillis = System.currentTimeMillis();
    }

    public void resetHourCounter() {
        this.hourCounter = 1;
        this.hourWindowStartMillis = System.currentTimeMillis();
    }

}
