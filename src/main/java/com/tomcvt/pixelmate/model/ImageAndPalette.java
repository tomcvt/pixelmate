package com.tomcvt.pixelmate.model;

import java.awt.image.BufferedImage;
import java.util.List;

public record ImageAndPalette(
    BufferedImage image,
    List<Integer> palette
) {
    
}
