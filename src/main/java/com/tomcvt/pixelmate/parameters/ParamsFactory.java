package com.tomcvt.pixelmate.parameters;

import java.util.Map;

public interface ParamsFactory<P extends OperationParameters> {
    P fromMap(Map<String, Object> params);
}
