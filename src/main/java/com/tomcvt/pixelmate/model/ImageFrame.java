package com.tomcvt.pixelmate.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.nio.Buffer;

import net.imglib2.img.Img;

public class ImageFrame {
    private Img<?> imgLib;
    private BufferedImage grayImage; // cached gray binary image for working with edge operations
    private BufferedImage binaryImage; // cached gray binary image for working with edge operations
    private BufferedImage bufferedImage;
    private Object fresh = null;
    private ImageType lastResult;

    public enum ImageType {
        IMGLIB,
        ARBG,
        GRAY,
        BINARY
    }

    public ImageFrame(Img<?> imgLib, BufferedImage bufferedImage) {
        if ((imgLib == null) == (bufferedImage == null)) {
            throw new IllegalStateException("Either imgLib or bufferedImage must be non-null, but not both.");
        }
        this.imgLib = imgLib;
        this.bufferedImage = bufferedImage;
        if (imgLib != null) {
            this.fresh = imgLib;
        } else {
            this.fresh = bufferedImage;
        }
    }
    public ImageFrame(Img<?> imgLib, BufferedImage bufferedImage, BufferedImage newImage, ImageType type) {
        this.imgLib = imgLib;
        this.bufferedImage = bufferedImage;
        this.lastResult = type;
        if (type == ImageType.BINARY) {
            this.binaryImage = newImage;
        } else if (type == ImageType.GRAY) {
            this.grayImage = newImage;
        }
        if (imgLib != null) {
            this.fresh = imgLib;
        } else {
            this.fresh = bufferedImage;
        }
    }

    public static ImageFrame fromImgLib(Img<?> imgLib) {
        return new ImageFrame(imgLib, null);
    }

    public static ImageFrame fromBufferedImage(BufferedImage bufferedImage) {
        return new ImageFrame(null, bufferedImage);
    }

    public static ImageFrame with(ImageFrame original, BufferedImage newImage, ImageType type) {
        return new ImageFrame(original.imgLib, original.bufferedImage, newImage, type);
    }

    public Object getFresh() {
        return fresh;
    }

    public Img<?> getImgLib() {
        return imgLib;
    }

    public BufferedImage getBufferedImage() {
        // TODO late apply conversion
        return bufferedImage;
    }

    public ImageType getLastResult() {
        return lastResult;
    }

    public BufferedImage getBufferedImage(ImageType type) {
        switch (type) {
            case IMGLIB:
                throw new IllegalArgumentException("ImageType IMGLIB is not supported in getImage()");
            case ARBG:
                return getBufferedImage();
            case GRAY:
                if (grayImage == null) {
                    grayImage = convertToGray(getBufferedImage());
                }
                return grayImage;
            case BINARY:
                if (binaryImage == null) {
                    binaryImage = convertToBinary(getBufferedImage());
                }
                return binaryImage;
            default:
                throw new IllegalArgumentException("Unknown image type: " + type);
        }
    }

    private BufferedImage convertToBinary(BufferedImage image) {
        BufferedImage binaryImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);
        ColorConvertOp op = new ColorConvertOp(image.getColorModel().getColorSpace(),
                binaryImage.getColorModel().getColorSpace(), null);
        op.filter(image, binaryImage);
        return binaryImage;
    }

    private BufferedImage convertToGray(BufferedImage image) {
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        ColorConvertOp op = new ColorConvertOp(image.getColorModel().getColorSpace(),
                grayImage.getColorModel().getColorSpace(), null);
        op.filter(image, grayImage);
        return grayImage;
    }
}
