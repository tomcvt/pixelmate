package com.tomcvt.pixelmate.dto;

import java.util.List;

public record OperationInfoDto(
    String name,
    List<ParamSpec> paramSpecs
) {
    
}
