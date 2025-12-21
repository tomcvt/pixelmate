package com.tomcvt.pixelmate.model.operations;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.model.OperationType;
import com.tomcvt.pixelmate.model.SimpleImageFrame;
import com.tomcvt.pixelmate.parameters.BlockSizeParams;

public class NearNeigbourRescale implements ImageOperationI<BlockSizeParams> {
    private static final String NAME = "NEAR_NEIGHBOUR_RESCALE";
    private final OperationType operationType = OperationType.COLOR;
    public static BlockSizeParams createDefaultPipelineParams() {
        return BlockSizeParams.DEFAULT_PARAMS;
    }
    @Override
    public OperationType getOperationType() {
        return operationType;
    }
    @Override
    public String getName() {
        return NAME;
    }
    @Override
    public BlockSizeParams getDefaultPipelineParameters() {
        return BlockSizeParams.DEFAULT_PARAMS;
    }
    @Override
    public BlockSizeParams parsePipelineParameters(Map<String, Object> values) {
        return BlockSizeParams.fromMap(values);
    }
    @Override
    public BlockSizeParams parsePipelineParameters(BlockSizeParams oldParams, Map<String, Object> values) {
        return BlockSizeParams.fromMapWithOldParams(oldParams, values);
    }
    @Override
    public List<ParamSpec> getParamSpecs() {
        return BlockSizeParams.getParamSpecs();
    }
    @Override
    public ImageFrame apply(ImageFrame input, BlockSizeParams parameters) {
        // No operation performed, just return the input as is.
        //FOR NOW not needed
        return input;
    }
    @Override
    public SimpleImageFrame applySimple(SimpleImageFrame input, BlockSizeParams parameters) {
        int blockSize = parameters.getBlockSize() != null ? parameters.getBlockSize() : 4;
        BufferedImage rescaledImage = rescaleNearestNeighbor(input.getColoredImage(), blockSize);
        return input.withColored(rescaledImage);
    }
    
    private BufferedImage rescaleNearestNeighbor(BufferedImage input, int blockSize) {
        int width = input.getWidth();
        int height = input.getHeight();
        int newWidth = width / blockSize + 1;
        int newHeight = height / blockSize + 1;

        BufferedImage downscaled = new BufferedImage(newWidth, newHeight, input.getType());

        for (int y = 0; y < newHeight - 1; y++) {
            for (int x = 0; x < newWidth - 1; x++) {
                int srcX = x * blockSize;
                int srcY = y * blockSize;
                downscaled.setRGB(x, y, input.getRGB(srcX, srcY));
            }
        }
        BufferedImage output = new BufferedImage(width, height, input.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int srcX = x / blockSize;
                int srcY = y / blockSize;
                output.setRGB(x, y, downscaled.getRGB(srcX, srcY));
            }
        }
        return output;
    }



}
