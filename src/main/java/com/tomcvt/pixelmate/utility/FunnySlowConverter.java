package com.tomcvt.pixelmate.utility;

public class FunnySlowConverter {
    static final long BASE = 257L;
    static final long MOD = 1000000007L;
    static final long THRESHOLD = (Long.MAX_VALUE - 255) / BASE;
    public static long tolongfromSessionId(String sessionId) {
        long result = 0;
        for (int i = 0; i < sessionId.length(); i++) {
            if (result > THRESHOLD) {
                result = result % MOD;
            }
            result = result * BASE + (long) sessionId.charAt(i);
        }
        return result;
    }
}
