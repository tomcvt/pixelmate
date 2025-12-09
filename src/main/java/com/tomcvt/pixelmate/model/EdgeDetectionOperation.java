package com.tomcvt.pixelmate.model;

import java.awt.image.BufferedImage;

import com.tomcvt.pixelmate.parameters.EdgeDetectionParams;

public class EdgeDetectionOperation implements ImageOperationI<EdgeDetectionParams> {
    private static final String NAME = "EDGE_DETECTION";

    public static EdgeDetectionOperation create() {
        return new EdgeDetectionOperation();
    }
    public static EdgeDetectionParams createDefaultParams() {
        EdgeDetectionParams params = new EdgeDetectionParams(128);
        return params;
    }
    public EdgeDetectionOperation() {
    }

    @Override
    public ImageFrame apply(ImageFrame inputImage, EdgeDetectionParams parameters) {
        int threshold = parameters.getThreshold() != null ? parameters.getThreshold() : 128;
        BufferedImage input = inputImage.getBufferedImage(ImageFrame.ImageType.GRAY);
        BufferedImage output = applySobel(input, threshold);
        return ImageFrame.with(inputImage, output, ImageFrame.ImageType.BINARY);
/*
        input = inputImage.getBufferedImage(ImageFrame.ImageType.GRAY);

        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage edges = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        threshold = parameters.getThreshold() != null ? parameters.getThreshold() : 128;

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int rgb = input.getRGB(x, y) & 0xFF;
                int gx = ((input.getRGB(x + 1, y) & 0xFF) - (input.getRGB(x - 1, y) & 0xFF));
                int gy = ((input.getRGB(x, y + 1) & 0xFF) - (input.getRGB(x, y - 1) & 0xFF));
                int g = Math.abs(gx) + Math.abs(gy);
                if (g > threshold)
                    edges.setRGB(x, y, 0xFF000000); // black
                else
                    edges.setRGB(x, y, 0xFFFFFFFF); // white
            }
        }
        return ImageFrame.fromBufferedImage(edges);
        */
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
