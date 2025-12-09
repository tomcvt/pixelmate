package com.tomcvt.pixelmate.pipeline;

import java.util.List;

import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.parameters.OperationParameters;

public class PipelineNode<P extends OperationParameters> {
    private final ImageOperationI<P> operation;
    private P parameters;
    private int index;

    public PipelineNode(ImageOperationI<P> operation, P parameters) {
        this.operation = operation;
        this.parameters = parameters;
    }

    public P getParameters() {
        return parameters;
    }

    public void setParameters(P parameters) {
        this.parameters = parameters;
    }

    public ImageOperationI<P> getOperation() {
        return operation;
    }

    public ImageFrame process(ImageFrame input) {
        ImageFrame result = operation.apply(input, parameters);
        return result;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

}
