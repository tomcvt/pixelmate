package com.tomcvt.pixelmate.parameters;

import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;

public class RadiusParams implements OperationParameters {
    public static final String PARAM_RADIUS = "radius";
    public static final String RADIUS_TYPE = "Integer";
    public static final Integer DEFAULT_RADIUS = 1;
    public static final Integer MIN_RADIUS = 0;
    public static final Integer MAX_RADIUS = 10;
    public static final ParamsFactory<RadiusParams> FACTORY = RadiusParams::fromMap;
    public static RadiusParams fromMap(Map<String, Object> params) {
        if (params == null) {
            return new RadiusParams(DEFAULT_RADIUS);
        }
        Integer radius = DEFAULT_RADIUS;
        if (params.containsKey(PARAM_RADIUS)) {
            try {
                radius = Integer.parseInt(params.get(PARAM_RADIUS).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid radius value: " + params.get(PARAM_RADIUS));
            }
            if (radius < MIN_RADIUS || radius > MAX_RADIUS) {
                throw new IllegalArgumentException("Radius value out of bounds: " + radius);
            }
        }
        return new RadiusParams(radius);
    }
    public static RadiusParams fromMapWithOldParams(RadiusParams oldParams, Map<String,Object> values) {
        if (values == null) {
            return oldParams;
        }
        Integer radius = oldParams.getRadius();
        if (values.containsKey(PARAM_RADIUS)) {
            try {
                radius = Integer.parseInt(values.get(PARAM_RADIUS).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid radius value: " + values.get(PARAM_RADIUS));
            }
            if (radius < MIN_RADIUS || radius > MAX_RADIUS) {
                throw new IllegalArgumentException("Radius value out of bounds: " + radius);
            }
        }
        return new RadiusParams(radius);
    }
    public static List<ParamSpec> getParamSpecs() {
        return List.of(
            new ParamSpec(PARAM_RADIUS, RADIUS_TYPE, DEFAULT_RADIUS, MIN_RADIUS, MAX_RADIUS)
        );
    }

    private final Integer radius;

    public RadiusParams(Integer radius) {
        this.radius = radius;
    }

    public Integer getRadius() {
        return radius;
    }
    
}
