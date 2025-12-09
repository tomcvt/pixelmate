package com.tomcvt.pixelmate.parameters;

public class EdgeDetectionParams implements OperationParameters {
    private final Integer threshold;
    public static EdgeDetectionParams withParams(Integer threshold) {
        return new EdgeDetectionParams(threshold);
    }

    public EdgeDetectionParams(Integer threshold) {
        this.threshold = threshold;
    }

    public Integer getThreshold() {
        return threshold;
    }
    
}
