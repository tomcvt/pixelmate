package com.tomcvt.pixelmate.model.operations;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.parameters.EdgeDetectionParams;

public class EdgeDetectionOperation implements ImageOperationI<EdgeDetectionParams> {
    public static final String NAME = "EDGE_DETECTION";

    public static EdgeDetectionParams createDefaultPipelineParams() {
        return new EdgeDetectionParams(EdgeDetectionParams.DEFAULT_THRESHOLD);
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
        BufferedImage output = applySobel(input, threshold);
        return ImageFrame.with(inputImage, output, ImageFrame.ImageType.BINARY);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public BufferedImage applySobel(BufferedImage input, int threshold) {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

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
                    output.setRGB(x, y, 0xFFFFFFFF); // white
            }
        }
        return output;
    }

}
