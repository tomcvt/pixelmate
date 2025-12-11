package com.tomcvt.pixelmate.parameters;

import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;

public class KMeansParams implements OperationParameters {
    private static final Integer DEFAULT_K = 8;
    protected static final String PARAM_K = "k";
    protected static final String K_TYPE = "Integer";
    private static final Integer MIN_K = 2;
    private static final Integer MAX_K = 128;
    private static final Integer DEFAULT_MAX_ITERATIONS = 15;
    protected static final String PARAM_MAX_ITERATIONS = "maxIterations";
    protected static final String MAX_ITERATIONS_TYPE = "Integer";
    private static final Integer MIN_MAX_ITERATIONS = 1;
    private static final Integer MAX_MAX_ITERATIONS = 50;
    private static final Double DEFAULT_EPS = 0.5;
    protected static final String PARAM_EPS = "eps";
    protected static final String EPS_TYPE = "Double";
    private static final Double MIN_EPS = 0.1;
    private static final Double MAX_EPS = 5.0;
    public static final ParamsFactory<KMeansParams> FACTORY = KMeansParams::fromMap;
    public static KMeansParams fromMap(Map<String, Object> params) {
        if (params == null) {
            return new KMeansParams(DEFAULT_K, DEFAULT_MAX_ITERATIONS, DEFAULT_EPS);
        }
        Integer k = DEFAULT_K;
        if (params.containsKey(PARAM_K)) {
            try {
                k = Integer.parseInt(params.get(PARAM_K).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid k value: " + params.get(PARAM_K));
            }
            if (k < MIN_K || k > MAX_K) {
                throw new IllegalArgumentException("k value out of bounds: " + k);
            }
        }
        Integer maxIterations = DEFAULT_MAX_ITERATIONS;
        if (params.containsKey(PARAM_MAX_ITERATIONS)) {
            try {
                maxIterations = Integer.parseInt(params.get(PARAM_MAX_ITERATIONS).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid maxIterations value: " + params.get(PARAM_MAX_ITERATIONS));
            }
            if (maxIterations < MIN_MAX_ITERATIONS || maxIterations > MAX_MAX_ITERATIONS) {
                throw new IllegalArgumentException("maxIterations value out of bounds: " + maxIterations);
            }
        }
        Double eps = DEFAULT_EPS;
        if (params.containsKey(PARAM_EPS)) {
            try {
                eps = Double.parseDouble(params.get(PARAM_EPS).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid eps value: " + params.get(PARAM_EPS));
            }
            if (eps < MIN_EPS || eps > MAX_EPS) {
                throw new IllegalArgumentException("eps value out of bounds: " + eps);
            }
        }
        return new KMeansParams(k, maxIterations, eps);
    }
    public static List<ParamSpec> getParamSpecs() {
        return List.of(
            new ParamSpec(PARAM_K, K_TYPE, DEFAULT_K, MIN_K, MAX_K),
            new ParamSpec(PARAM_MAX_ITERATIONS, MAX_ITERATIONS_TYPE, DEFAULT_MAX_ITERATIONS, MIN_MAX_ITERATIONS, MAX_MAX_ITERATIONS),
            new ParamSpec(PARAM_EPS, EPS_TYPE, DEFAULT_EPS, MIN_EPS, MAX_EPS)
        );
    }

    private final Integer k;
    private final Integer maxIterations;
    private final Double eps;
    public KMeansParams(Integer k, Integer maxIterations, Double eps) {
        this.k = k;
        this.maxIterations = maxIterations;
        this.eps = eps;
    }
    public Integer getK() {
        return k;
    }
    public Integer getMaxIterations() {
        return maxIterations;
    }
    public Double getEps() {
        return eps;
    }

}
