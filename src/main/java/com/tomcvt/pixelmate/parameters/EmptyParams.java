package com.tomcvt.pixelmate.parameters;

import java.util.Map;

public class EmptyParams implements OperationParameters {
    public static final ParamsFactory<EmptyParams> FACTORY = EmptyParams::fromMap;
    public static EmptyParams fromMap(Map<String, Object> params) {
        return new EmptyParams();
    }

    public EmptyParams() {
    }
}
