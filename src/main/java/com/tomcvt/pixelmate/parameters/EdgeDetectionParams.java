package com.tomcvt.pixelmate.parameters;

import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;

public class EdgeDetectionParams implements OperationParameters {
    public static final Integer DEFAULT_THRESHOLD = 128;
    public static final String PARAM_THRESHOLD = "threshold";
    public static final String THRESHOLD_TYPE = "Integer";
    public static final Integer MIN_THRESHOLD = 0;
    public static final Integer MAX_THRESHOLD = 255;
    public static final ParamsFactory<EdgeDetectionParams> FACTORY = EdgeDetectionParams::fromMap;
    public static EdgeDetectionParams fromMap(Map<String, Object> params) {
        Integer threshold = DEFAULT_THRESHOLD;
        if (params.containsKey(PARAM_THRESHOLD)) {
            try {
                threshold = Integer.parseInt(params.get(PARAM_THRESHOLD).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid threshold value: " + params.get(PARAM_THRESHOLD));
            }
            
        }
        return new EdgeDetectionParams(threshold);
    }
    public static List<ParamSpec> getParamSpecs() {
        return List.of(
            new ParamSpec(PARAM_THRESHOLD, THRESHOLD_TYPE, DEFAULT_THRESHOLD, MIN_THRESHOLD, MAX_THRESHOLD)
        );
    }

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
