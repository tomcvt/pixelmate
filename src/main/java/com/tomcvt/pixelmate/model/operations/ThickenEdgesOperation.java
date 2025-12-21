package com.tomcvt.pixelmate.model.operations;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;


import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.model.OperationType;
import com.tomcvt.pixelmate.model.SimpleImageFrame;

import com.tomcvt.pixelmate.parameters.RadiusParams;

public class ThickenEdgesOperation implements ImageOperationI<RadiusParams> {
    public static final String NAME = "THICKEN_EDGES";
    public final OperationType operationType = OperationType.EDGE;

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    public static RadiusParams createDefaultPipelineParams() {
        return new RadiusParams(RadiusParams.DEFAULT_RADIUS);
    }
    @Override
    public List<ParamSpec> getParamSpecs() {
        return RadiusParams.getParamSpecs();
    }
    @Override
    public RadiusParams parsePipelineParameters(Map<String, Object> values) {
        return RadiusParams.fromMap(values);
    }
    @Override
    public RadiusParams parsePipelineParameters(RadiusParams oldParams, Map<String, Object> values) {
        return RadiusParams.fromMapWithOldParams(oldParams, values);
    }
    @Override
    public RadiusParams getDefaultPipelineParameters() {
        return createDefaultPipelineParams();
    }

    @Override
    public ImageFrame apply(ImageFrame input, RadiusParams parameters) {
        BufferedImage edges = input.getConvertedBufferedImageForOperationByType(ImageFrame.ImageType.BINARY, ImageFrame.EditPath.GRAYSCALE);
        if (parameters.getRadius() == 0) {
            return ImageFrame.with(input, edges, ImageFrame.ImageType.BINARY);
            //return input;
            //TODO think about it and consequences
        }
        int width = edges.getWidth();
        int height = edges.getHeight();
        BufferedImage thick = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int radius = parameters.getRadius() != null ? parameters.getRadius() : 1;
        for (int y = radius; y < height - radius; y++) {
            for (int x = radius; x < width - radius; x++) {
                boolean black = false;
                for (int dy = -radius; dy <= radius; dy++)
                    for (int dx = -radius; dx <= radius; dx++)
                        if ((edges.getRGB(x + dx, y + dy) & 0x00FFFFFF) == 0x00000000)
                            black = true;
                thick.setRGB(x, y, black ? 0xFF000000 : edges.getRGB(x, y));
            }
        }
        return ImageFrame.with(input, thick, ImageFrame.ImageType.BINARY);
    }
    @Override
    public SimpleImageFrame applySimple(SimpleImageFrame input, RadiusParams parameters) {
        BufferedImage edges = input.getEdgeImage();
        if (parameters.getRadius() == 0) {
            return input.withEdge(edges);
        }
        int radius = parameters.getRadius() != null ? parameters.getRadius() : 1;
        BufferedImage thick = thickenEdges(edges, radius);
        return input.withEdge(thick);
    }
    @Override
    public String getName() {
        return NAME;
    }


    // from edges op we get either black or transparent white pixels as edges
    // so only 0xFF000000 is edge, everything else is non-edge
    private BufferedImage thickenEdges(BufferedImage edges, int radius) {
        int width = edges.getWidth();
        int height = edges.getHeight();
        BufferedImage thick = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = radius; y < height - radius; y++) {
            for (int x = radius; x < width - radius; x++) {
                boolean black = false;
                for (int dy = -radius; dy <= radius; dy++)
                    for (int dx = -radius; dx <= radius; dx++) {
                        
                        if (edges.getRGB(x + dx, y + dy) == 0xFF000000)
                            black = true;
                    }
                thick.setRGB(x, y, black ? 0xFF000000 : edges.getRGB(x, y));
            }
        }
        return thick;
    }

}
