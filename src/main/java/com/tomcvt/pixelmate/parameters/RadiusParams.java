package com.tomcvt.pixelmate.parameters;

public class RadiusParams implements OperationParameters {
    private final Integer radius;

    public RadiusParams(Integer radius) {
        this.radius = radius;
    }

    public Integer getRadius() {
        return radius;
    }
    
}
