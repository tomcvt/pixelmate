package com.tomcvt.pixelmate.model.operations;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.model.OperationType;
import com.tomcvt.pixelmate.model.SimpleImageFrame;
import com.tomcvt.pixelmate.parameters.EdgeQuantizationParams;

public class EdgeQuantizationOperation implements ImageOperationI<EdgeQuantizationParams> {
    private static final String OPERATION_NAME = "EDGE_QUANTIZATION";
    private final OperationType operationType = OperationType.EDGE;

    @Override
    public String getName() {
        return OPERATION_NAME;
    }
    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    public static EdgeQuantizationParams createDefaultPipelineParams() {
        return EdgeQuantizationParams.DEFAULT_PARAMS;
    }

    @Override
    public EdgeQuantizationParams getDefaultPipelineParameters() {
        return createDefaultPipelineParams();
    }

    @Override
    public EdgeQuantizationParams parsePipelineParameters(Map<String, Object> values) {
        return EdgeQuantizationParams.fromMap(values);
    }

    @Override
    public EdgeQuantizationParams parsePipelineParameters(EdgeQuantizationParams oldParams,
            Map<String, Object> values) {
        return EdgeQuantizationParams.fromMapWithOldParams(oldParams, values);
    }

    @Override
    public List<ParamSpec> getParamSpecs() {
        return EdgeQuantizationParams.getParamSpecs();
    }

    @Override
    public ImageFrame apply(ImageFrame input, EdgeQuantizationParams parameters) {
        // TODO To implemnt
        return null;
    }

    //TODO implement from sttaic defaults in every operation
    @Override
    public SimpleImageFrame applySimple(SimpleImageFrame input, EdgeQuantizationParams parameters) {
        int blockSize = parameters.getBlockSize() != null ? parameters.getBlockSize() : EdgeQuantizationParams.DEFAULT_BLOCK_SIZE;
        float coverageThreshold = parameters.getThreshold() != null ? parameters.getThreshold() : EdgeQuantizationParams.DEFAULT_THRESHOLD;

        var inputEdges = input.getEdgeImage();
        var edgesGrid = downsampleEdgeMask(inputEdges, blockSize, coverageThreshold);
        var outputEdges = expandGrid(edgesGrid, blockSize, inputEdges.getWidth(), inputEdges.getHeight());
        return input.withEdge(outputEdges);
    }

    private BufferedImage downsampleEdgeMask(
            BufferedImage input,
            int blockSize,
            float coverageThreshold
    ) {
        int width = input.getWidth();
        int height = input.getHeight();

        int gridW = (int) Math.ceil((double) width / blockSize);
        int gridH = (int) Math.ceil((double) height / blockSize);

        BufferedImage grid = new BufferedImage(
                gridW,
                gridH,
                BufferedImage.TYPE_INT_ARGB);

        int blockArea = blockSize * blockSize;

        for (int gy = 0; gy < gridH; gy++) {
            for (int gx = 0; gx < gridW; gx++) {

                int blackCount = 0;

                for (int y = 0; y < blockSize; y++) {
                    int srcY = gy * blockSize + y;
                    if (srcY >= height)
                        break;

                    for (int x = 0; x < blockSize; x++) {
                        int srcX = gx * blockSize + x;
                        if (srcX >= width)
                            break;

                        int argb = input.getRGB(srcX, srcY);
                        int alpha = (argb >>> 24) & 0xFF;

                        // black edge pixel = opaque
                        if (alpha > 0) {
                            blackCount++;
                        }
                    }
                }

                float coverage = (float) blackCount / blockArea;

                if (coverage >= coverageThreshold) {
                    grid.setRGB(gx, gy, 0xFF000000); // black
                } else {
                    grid.setRGB(gx, gy, 0x00000000); // transparent
                }
            }
        }

        return grid;
    }

    private BufferedImage expandGrid(
            BufferedImage grid,
            int blockSize,
            int targetWidth,
            int targetHeight) {
        BufferedImage output = new BufferedImage(
                targetWidth,
                targetHeight,
                BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {

                int gx = x / blockSize;
                int gy = y / blockSize;

                int color = grid.getRGB(gx, gy);
                output.setRGB(x, y, color);
            }
        }

        return output;
    }
}
