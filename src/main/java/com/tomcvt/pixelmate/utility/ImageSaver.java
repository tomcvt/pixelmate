package com.tomcvt.pixelmate.utility;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageSaver {
    private final static Logger log = LoggerFactory.getLogger(ImageSaver.class);

    public static boolean clearSessionFolder(String path, String session) {
        try {
            File dir = new File(path + "/" + session);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                return dir.delete();
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Error clearing session folder: ", e);
            return false;
        }
    }

    public static boolean deleteImage(String path, String session, String imageName) {
        try {
            File file = new File(path + "/" + session + "/" + imageName + ".png");
            if (file.exists()) {
                return file.delete();
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting image: ", e);
            return false;
        }
    }

    public static String saveImage(String path, String session, String imageName, BufferedImage image) {
        try {
            File outputfile = new File(path + "/" + session);
            if (!outputfile.exists())
                outputfile.mkdirs();
            String relPath = session + "/" + imageName + ".png";
            ImageIO.write(image, "png", new File(outputfile, imageName + ".png"));
            return relPath;
        } catch (Exception e) {
            log.error("Error saving image: ", e);
            return null;
        }
    }

    public static BufferedImage loadImage(String path, String session, String imageName) {
        try {
            File inputfile = new File(path + "/" + session + "/" + imageName + ".png");
            if (inputfile.exists()) {
                return ImageIO.read(inputfile);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error loading image: ", e);
            throw new RuntimeException(e);
            //return null;
        }
    }

}
