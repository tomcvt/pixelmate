package com.tomcvt.pixelmate.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.parameters.OperationParameters;

public class PipelineBuilder {
    private final List<PipelineNode<?>> nodes = new ArrayList<>();

    public static PipelineBuilder builder() {
        return new PipelineBuilder();
    }

    public PipelineBuilder() {
    }

    public <P extends OperationParameters> PipelineBuilder add(ImageOperationI<P> operation, P parameters) {
        PipelineNode<P> node = new PipelineNode<>(operation, parameters);
        node.setIndex(nodes.size());
        nodes.add(node);
        return this;
    }

    public OperationsPipeline buildNodes(OperationsPipeline existingPipeline) {
        existingPipeline.getNodes().clear();
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setIndex(i);
        }
        existingPipeline.setNodes(nodes);
        existingPipeline.clearCacheAndUrls();
        return existingPipeline;
    }

    public SimpleOperationsPipeline buildSimpleNodes(SimpleOperationsPipeline existingPipeline) {
        existingPipeline.getNodes().clear();
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setIndex(i);
        }
        existingPipeline.setNodes(nodes);
        existingPipeline.clearCacheAndUrls();
        return existingPipeline;
    }
}
