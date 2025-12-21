package com.tomcvt.pixelmate.utility;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

public class ImageReader {
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImageReader.class);
    public static BufferedImage loadImage(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            BufferedImage image = ImageIO.read(is);
            String colorModel = image.getColorModel().toString();
            log.info("Loaded image with color model: {}", colorModel);
            return image;
        } catch (Exception e) {
            log.error("Error loading image: ", e);
            throw new RuntimeException("Failed to load image", e);
        }
    }
}
