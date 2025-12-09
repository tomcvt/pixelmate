package com.tomcvt.pixelmate.model;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.parameters.RadiusParams;

public class ThickenEdgesOperation implements ImageOperationI<RadiusParams> {
    public static final String NAME = "THICKEN_EDGES";

    public static RadiusParams createDefaultParams() {
        RadiusParams params = new RadiusParams(1);
        return params;
    }
    @Override
    public List<ParamSpec> getParamSpecs() {
        return RadiusParams.getParamSpecs();
    }
    @Override
    public RadiusParams parseParameters(Map<String, Object> values) {
        return RadiusParams.fromMap(values);
    }

    @Override
    public ImageFrame apply(ImageFrame input, RadiusParams parameters) {
        BufferedImage edges = input.getBufferedImage(ImageFrame.ImageType.BINARY);
        if (edges.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            throw new IllegalArgumentException("Input image must be binary (black and white)");
        }
        int width = edges.getWidth();
        int height = edges.getHeight();
        BufferedImage thick = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        int radius = parameters.getRadius() != null ? parameters.getRadius() : 1;
        for (int y = radius; y < height - radius; y++) {
            for (int x = radius; x < width - radius; x++) {
                boolean black = false;
                for (int dy = -radius; dy <= radius; dy++)
                    for (int dx = -radius; dx <= radius; dx++)
                        if ((edges.getRGB(x + dx, y + dy) & 0x00FFFFFF) == 0x00000000)
                            black = true;
                thick.setRGB(x, y, black ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return ImageFrame.with(input, thick, ImageFrame.ImageType.BINARY);
    }
    @Override
    public String getName() {
        return NAME;
    }

}
