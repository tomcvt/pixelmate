package com.tomcvt.pixelmate.model;

import java.util.Map;

import com.tomcvt.pixelmate.parameters.OperationParameters;

public abstract class ImageOperation<P extends OperationParameters> implements ImageOperationI<P> {
    protected final P params;
    public abstract String getName();
    public abstract ImageFrame apply(ImageFrame input, P parameters);
    public abstract OperationParameters createDefaultParameters();

    public ImageOperation(Map<String, Object> params) {
        this.params = fromMap(params);
    }

    protected abstract P fromMap(Map<String, Object> params);
    
}
