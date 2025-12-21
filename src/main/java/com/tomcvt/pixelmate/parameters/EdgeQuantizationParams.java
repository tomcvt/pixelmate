package com.tomcvt.pixelmate.parameters;

import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;

public class EdgeQuantizationParams implements OperationParameters {
    public static final String PARAM_BLOCK_SIZE = "blockSize";
    public static final String PARAM_THRESHOLD = "threshold";
    public static final String BLOCK_SIZE_TYPE = "Integer(2^n)";
    public static final String THRESHOLD_TYPE = "Float";
    public static final Integer DEFAULT_BLOCK_SIZE = 4;
    public static final Float DEFAULT_THRESHOLD = 0.15f;
    public static final Integer MIN_BLOCK_SIZE = 1;
    public static final Integer MAX_BLOCK_SIZE = 64;
    public static final Float MIN_THRESHOLD = 0.0f;
    public static final Float MAX_THRESHOLD = 0.20f;
    public static final EdgeQuantizationParams DEFAULT_PARAMS = new EdgeQuantizationParams(DEFAULT_BLOCK_SIZE, DEFAULT_THRESHOLD);
    public static final ParamsFactory<EdgeQuantizationParams> FACTORY = EdgeQuantizationParams::fromMap;
        public static EdgeQuantizationParams fromMap(Map<String, Object> params) {
            if (params == null) {
                return new EdgeQuantizationParams(DEFAULT_BLOCK_SIZE, DEFAULT_THRESHOLD);
            }
            Integer blockSize = DEFAULT_BLOCK_SIZE;
            if (params.containsKey(PARAM_BLOCK_SIZE)) {
                try {
                    blockSize = Integer.parseInt(params.get(PARAM_BLOCK_SIZE).toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid block size value: " + params.get(PARAM_BLOCK_SIZE));
                }
                if (blockSize < MIN_BLOCK_SIZE || blockSize > MAX_BLOCK_SIZE) {
                    throw new IllegalArgumentException("Block size value out of bounds: " + blockSize);
                }
            }
            Float threshold = DEFAULT_THRESHOLD;
            if (params.containsKey(PARAM_THRESHOLD)) {
                try {
                    threshold = Float.parseFloat(params.get(PARAM_THRESHOLD).toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid threshold value: " + params.get(PARAM_THRESHOLD));
                }
                if (threshold < MIN_THRESHOLD || threshold > MAX_THRESHOLD) {
                    throw new IllegalArgumentException("THRESHOLD value out of bounds: " + threshold);
                }
            }
            return new EdgeQuantizationParams(blockSize, threshold);
        }

    public static EdgeQuantizationParams fromMapWithOldParams(EdgeQuantizationParams oldParams, Map<String,Object> values) {
        if (values == null) {
            return oldParams;
        }
        Integer blockSize = oldParams.getBlockSize();
        if (values.containsKey(PARAM_BLOCK_SIZE)) {
            try {
                blockSize = Integer.parseInt(values.get(PARAM_BLOCK_SIZE).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid block size value: " + values.get(PARAM_BLOCK_SIZE));
            }
            if (blockSize < MIN_BLOCK_SIZE || blockSize > MAX_BLOCK_SIZE) {
                throw new IllegalArgumentException("Block size value out of bounds: " + blockSize + " : " + getRangeString());
            }
        }
        Float threshold = oldParams.getThreshold();
        if (values.containsKey(PARAM_THRESHOLD)) {
            try {
                threshold = Float.parseFloat(values.get(PARAM_THRESHOLD).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid threshold value: " + values.get(PARAM_THRESHOLD));
            }
            if (threshold < MIN_THRESHOLD || threshold > MAX_THRESHOLD) {
                throw new IllegalArgumentException("THRESHOLD value out of bounds: " + threshold + " : " + getRangeString());
            }
        }
        return new EdgeQuantizationParams(blockSize, threshold);
    }
    public static List<ParamSpec> getParamSpecs() {
        return List.of(
            new ParamSpec(PARAM_BLOCK_SIZE, BLOCK_SIZE_TYPE, DEFAULT_BLOCK_SIZE, MIN_BLOCK_SIZE, MAX_BLOCK_SIZE),
            new ParamSpec(PARAM_THRESHOLD, THRESHOLD_TYPE, DEFAULT_THRESHOLD, MIN_THRESHOLD, MAX_THRESHOLD)
        );
    }
    private Integer blockSize;
    private Float threshold;

    public EdgeQuantizationParams(Integer blockSize, Float threshold) {
        this.blockSize = blockSize;
        this.threshold = threshold;
    }

    public Integer getBlockSize() {
        return blockSize;
    }

    public Float getThreshold() {
        return threshold;
    }

    private static String getRangeString() {
        return " BlockSize: [" + MIN_BLOCK_SIZE + " - " + MAX_BLOCK_SIZE + "], THRESHOLD: [" + MIN_THRESHOLD + " - " + MAX_THRESHOLD + "]";
    }
}
