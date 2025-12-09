package com.tomcvt.pixelmate.dto;

import java.util.Map;

public record ParamInput(
    Integer index,
    Map<String, Object> values
) {
    
}
