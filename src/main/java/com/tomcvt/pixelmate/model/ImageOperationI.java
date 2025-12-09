package com.tomcvt.pixelmate.model;

import com.tomcvt.pixelmate.parameters.OperationParameters;

public interface ImageOperationI<P extends OperationParameters> {
    ImageFrame apply(ImageFrame input, P parameters);
    String getName();
}