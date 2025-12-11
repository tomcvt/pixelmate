package com.tomcvt.pixelmate.model;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import com.tomcvt.pixelmate.exceptions.LossyConversionException;

import net.imglib2.img.Img;

public class ImageFrame {
    private Img<?> imgLib;
    private BufferedImage grayImage; // cached gray binary image for working with edge operations
    private BufferedImage binaryImage; // cached black-white binary image for working with threshold operations
    private BufferedImage coloredBuffered;
    private ImageType lastGray; // last type used gray or binary
    private ImageType lastColored; // last type used color or imglib
    private ImageType lastType; // last type used overall

    public enum ImageType {
        IMGLIB,
        ARGB,
        GRAY,
        BINARY
    }

    public enum EditPath {
        COLOR,
        GRAYSCALE
    }

    public ImageFrame(Img<?> imgLib, BufferedImage coloredBuffered) {
        if ((imgLib == null) == (coloredBuffered == null)) {
            throw new IllegalStateException("Either imgLib or coloredBuffered must be non-null, but not both.");
        }
        this.imgLib = imgLib;
        this.coloredBuffered = coloredBuffered;
        this.lastType = (imgLib != null) ? ImageType.IMGLIB : ImageType.ARGB;
        this.lastColored = this.lastType;
        this.lastGray = ImageType.GRAY; // default to GRAY

    }
    public ImageFrame(Img<?> imgLib, BufferedImage coloredBuffered, Object newImage, ImageType lastGray, ImageType lastColored, ImageType lastType) {
        this.imgLib = imgLib;
        this.coloredBuffered = coloredBuffered;
        this.lastGray = lastGray;
        this.lastColored = lastColored;
        this.lastType = lastType;
        switch (lastType) {
            case IMGLIB:
                this.imgLib = (Img<?>) newImage;
                this.coloredBuffered = null;
                break;
            case ARGB:
                this.coloredBuffered = (BufferedImage) newImage;
                this.imgLib = null;
                break;
            case GRAY:
                this.grayImage = (BufferedImage) newImage;
                break;
            case BINARY:
                this.binaryImage = (BufferedImage) newImage;
                break;
            default:
                throw new IllegalArgumentException("Unknown image type: " + lastType);
        }
    }

    public static ImageFrame fromImgLib(Img<?> imgLib) {
        return new ImageFrame(imgLib, null);
    }

    public static ImageFrame fromBufferedImage(BufferedImage coloredBuffered) {
        return new ImageFrame(null, coloredBuffered);
    }

    public static ImageFrame with(ImageFrame original, BufferedImage newImage, ImageType lastType) {
        switch (lastType) {
            case IMGLIB:
                return new ImageFrame(original.imgLib, null, newImage, original.lastGray, lastType, lastType);
            case ARGB:
                return new ImageFrame(null, original.coloredBuffered, newImage, original.lastGray, lastType, lastType);
            case GRAY:
                return new ImageFrame(original.imgLib, original.coloredBuffered, newImage, lastType, original.lastColored, lastType);
            case BINARY:
                return new ImageFrame(original.imgLib, original.coloredBuffered, newImage, lastType, original.lastColored, lastType);
            default:
                throw new IllegalArgumentException("Unknown image type: " + lastType);
        }
    }

    public Img<?> getImgLib() {
        return imgLib;
    }

    public BufferedImage getColoredBuffered() {
        return coloredBuffered;
    }

    public ImageType getLastGrey() {
        return lastGray;
    }

    public ImageType getLastColored() {
        return lastColored;
    }
    public BufferedImage getAsBufferedLast() {
        switch (lastType) {
            case IMGLIB:
                throw new IllegalArgumentException("ImageType IMGLIB is not supported in getImage()");
            case ARGB:
                return coloredBuffered;
            case GRAY:
                return grayImage;
            case BINARY:
                return binaryImage;
            default:
                throw new IllegalArgumentException("Unknown image type: " + lastType);
        }
    }

    public BufferedImage getConvertedBufferedImageForOperationByType(ImageType type, EditPath path) {
        var fromType = lastType;
        if (path == EditPath.GRAYSCALE) {
            fromType = lastGray;
        } else if (path == EditPath.COLOR) {
            fromType = lastColored;
        }
        switch (type) {
            case IMGLIB:
                throw new IllegalArgumentException("ImageType IMGLIB is not supported in getImage()");
            case ARGB:
                switch (fromType) {
                    case ARGB:
                        return coloredBuffered;
                    case IMGLIB:
                        throw new UnsupportedOperationException("Conversion from ImgLib to BufferedImage not implemented");
                    case GRAY:
                        return convertToARGB(grayImage);
                    case BINARY:
                        return convertToARGB(binaryImage);
                    default:
                        throw new IllegalArgumentException("Unknown image type: " + fromType);
                }
            case GRAY:
                switch (fromType) {
                    case GRAY:
                        return grayImage;
                    case BINARY:
                        return convertToGray(binaryImage);
                    case IMGLIB:
                        throw new UnsupportedOperationException("Conversion from ImgLib to BufferedImage not implemented");
                    case ARGB:
                        return convertToGray(coloredBuffered);
                    default:
                        throw new IllegalArgumentException("Unknown image type: " + fromType);
                }
            case BINARY:
                switch (fromType) {
                    case BINARY:
                        return binaryImage;
                    case GRAY:
                        throw new LossyConversionException("GRAY to BINARY conversion is lossy and not supported.");
                    case IMGLIB:
                        throw new UnsupportedOperationException("Conversion from ImgLib to BufferedImage not implemented");
                    case ARGB:
                        throw new LossyConversionException("ARGB to BINARY conversion is lossy and not supported.");
                    default:
                        throw new IllegalArgumentException("Unknown image type: " + fromType);
                }
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
    private BufferedImage convertToARGB(BufferedImage image) {
        BufferedImage argbImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        ColorConvertOp op = new ColorConvertOp(image.getColorModel().getColorSpace(),
                argbImage.getColorModel().getColorSpace(), null);
        op.filter(image, argbImage);
        return argbImage;
    }
}
