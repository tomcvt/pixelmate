package com.tomcvt.pixelmate.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class SimpleImageFrame {
    private BufferedImage coloredImage;
    private BufferedImage edgeImage; // cached edge image for working with edge operations
    private ImageType lastType; // last type used overall

    public enum ImageType {
        ARGB,
        EDGE
    }

    public SimpleImageFrame(BufferedImage coloredImage, BufferedImage edgeImage, ImageType lastType) {
        this.coloredImage = coloredImage;
        this.edgeImage = edgeImage;
        this.lastType = lastType;
    }

    public static SimpleImageFrame fromBufferedImage(BufferedImage image) {
        var color = deepCopyARGB(image);
        var edge = deepCopyARGB(image);
        return new SimpleImageFrame(color, edge, ImageType.ARGB);
    }

    public SimpleImageFrame withEdge(BufferedImage edgeImage) {
        return new SimpleImageFrame(this.coloredImage, edgeImage, ImageType.EDGE);
    }

    public SimpleImageFrame withColored(BufferedImage coloredImage) {
        return new SimpleImageFrame(coloredImage, this.edgeImage, ImageType.ARGB);
    }

    public BufferedImage getColoredImage() {
        return coloredImage;
    }

    public BufferedImage getEdgeImage() {
        return edgeImage;
    }

    public ImageType getLastType() {
        return lastType;
    }

    public void setLastType(ImageType lastType) {
        this.lastType = lastType;
    }

    public BufferedImage getCurrentImage() {
        if (lastType == ImageType.ARGB) {
            return coloredImage;
        } else {
            return edgeImage;
        }
    }

    private static BufferedImage deepCopyARGB(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return copy;
    }


}
