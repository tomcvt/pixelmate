package com.tomcvt.pixelmate.model.operations;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.rmi.server.Operation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.model.OperationType;
import com.tomcvt.pixelmate.model.SimpleImageFrame;
import com.tomcvt.pixelmate.parameters.EdgeDetectionParams;

public class EdgeDetectionOperation implements ImageOperationI<EdgeDetectionParams> {
    public static final String NAME = "EDGE_DETECTION";
    public final OperationType operationType = OperationType.EDGE;

    public static EdgeDetectionParams createDefaultPipelineParams() {
        return new EdgeDetectionParams(EdgeDetectionParams.DEFAULT_THRESHOLD);
    }
    @Override
    public OperationType getOperationType() {
        return operationType;
    }
    
    @Override
    public EdgeDetectionParams getDefaultPipelineParameters() {
        return createDefaultPipelineParams();
    }
    @Override
    public EdgeDetectionParams parsePipelineParameters(Map<String, Object> values) {
        return EdgeDetectionParams.fromMap(values);
    }
    @Override
    public EdgeDetectionParams parsePipelineParameters(EdgeDetectionParams oldParams, Map<String, Object> values) {
        return EdgeDetectionParams.fromMapWithOldParams(oldParams, values);
    }

    @Override
    public List<ParamSpec> getParamSpecs() {
        return EdgeDetectionParams.getParamSpecs();
    }

    @Override
    public ImageFrame apply(ImageFrame inputImage, EdgeDetectionParams parameters) {
        int threshold = parameters.getThreshold() != null ? parameters.getThreshold() : 128;
        BufferedImage input = inputImage.getConvertedBufferedImageForOperationByType(ImageFrame.ImageType.GRAY, ImageFrame.EditPath.COLOR);
        if (threshold >= EdgeDetectionParams.MAX_THRESHOLD) {
            BufferedImage output = noEdge(input);
            return ImageFrame.with(inputImage, output, ImageFrame.ImageType.BINARY);
        }
        BufferedImage output = applySobel(input, threshold);
        return ImageFrame.with(inputImage, output, ImageFrame.ImageType.BINARY);
    }

    @Override
    public SimpleImageFrame applySimple(SimpleImageFrame inputImage, EdgeDetectionParams parameters) {
        int threshold = parameters.getThreshold() != null ? parameters.getThreshold() : 128;
        if (threshold >= EdgeDetectionParams.MAX_THRESHOLD) {
            BufferedImage output = noEdge(inputImage.getEdgeImage());
            return inputImage.withEdge(output);
        }
        BufferedImage input = inputImage.getEdgeImage();
        BufferedImage grayInput = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayInput.getGraphics().drawImage(input, 0, 0, null);
        BufferedImage output = applySobel(grayInput, threshold);
        return inputImage.withEdge(output);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public BufferedImage applySobel(BufferedImage input, int threshold) {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[][] gx = {
                { -1, 0, 1 },
                { -2, 0, 2 },
                { -1, 0, 1 }
        };
        int[][] gy = {
                { -1, -2, -1 },
                { 0, 0, 0 },
                { 1, 2, 1 }
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumX = 0, sumY = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int pixel = input.getRGB(x + kx, y + ky) & 0xFF;
                        sumX += gx[ky + 1][kx + 1] * pixel;
                        sumY += gy[ky + 1][kx + 1] * pixel;
                    }
                }
                int g = Math.abs(sumX) + Math.abs(sumY);
                if (g > threshold)
                    output.setRGB(x, y, 0xFF000000); // black
                else
                    output.setRGB(x, y, 0x00FFFFFF); // white
            }
        }

        for (int x = 0; x < width; x++) {
            output.setRGB(x, 0, 0x00FFFFFF);
            output.setRGB(x, height - 1, 0x00FFFFFF);
        }
        for (int y = 0; y < height; y++) {
            output.setRGB(0, y, 0x00FFFFFF);
            output.setRGB(width - 1, y, 0x00FFFFFF);
        }
        return output;
    }

    private BufferedImage noEdge(BufferedImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] data = new int[width * height];
        Arrays.fill(data, 0x00FFFFFF); 
        output.setRGB(0, 0, width, height, data, 0, width);
        return output;
    }

}
