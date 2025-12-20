package com.tomcvt.pixelmate.model;

import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.parameters.OperationParameters;

public interface ImageOperationI<P extends OperationParameters> {
    ImageFrame apply(ImageFrame input, P parameters);
    SimpleImageFrame applySimple(SimpleImageFrame input, P parameters);
    String getName();
    List<ParamSpec> getParamSpecs();
    P parsePipelineParameters(Map<String, Object> values);
    P parsePipelineParameters(P oldParams, Map<String, Object> values);
    P getDefaultPipelineParameters();
}