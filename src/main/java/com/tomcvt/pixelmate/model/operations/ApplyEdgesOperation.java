package com.tomcvt.pixelmate.model.operations;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.model.OperationType;
import com.tomcvt.pixelmate.model.SimpleImageFrame;
import com.tomcvt.pixelmate.parameters.EmptyParams;

public class ApplyEdgesOperation implements ImageOperationI<EmptyParams> {
    public static final String NAME = "APPLY_EDGES";
    public final OperationType operationType = OperationType.COLOR;

    public static EmptyParams createDefaultPipelineParams() {
        return new EmptyParams();
    }
    @Override
    public OperationType getOperationType() {
        return operationType;
    }
    @Override
    public List<ParamSpec> getParamSpecs() {
        return List.of();
    }
    @Override
    public EmptyParams getDefaultPipelineParameters() {
        return createDefaultPipelineParams();
    }
    @Override
    public EmptyParams parsePipelineParameters(Map<String, Object> values) {
        return new EmptyParams();
    }
    @Override
    public EmptyParams parsePipelineParameters(EmptyParams oldParams, Map<String, Object> values) {
        return new EmptyParams();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ImageFrame apply(ImageFrame input, EmptyParams parameters) {
        BufferedImage edgesImage = input.getConvertedBufferedImageForOperationByType(ImageFrame.ImageType.BINARY, ImageFrame.EditPath.GRAYSCALE);
        BufferedImage coloredImage = input.getConvertedBufferedImageForOperationByType(ImageFrame.ImageType.ARGB, ImageFrame.EditPath.COLOR);
        int width = edgesImage.getWidth();
        int height = edgesImage.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.drawImage(coloredImage, 0, 0, null);
        g.drawImage(edgesImage, 0, 0, null);
        g.dispose();
        return ImageFrame.with(input, output, ImageFrame.ImageType.ARGB);
    }

    @Override
    public SimpleImageFrame applySimple(SimpleImageFrame input, EmptyParams parameters) {
        BufferedImage edgesImage = input.getEdgeImage();
        BufferedImage coloredImage = input.getColoredImage();
        int width = edgesImage.getWidth();
        int height = edgesImage.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.drawImage(coloredImage, 0, 0, null);
        g.drawImage(edgesImage, 0, 0, null);
        g.dispose();
        return input.withColored(output);
    }
    
}
