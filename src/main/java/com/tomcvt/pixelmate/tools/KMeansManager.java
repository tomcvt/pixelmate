package com.tomcvt.pixelmate.tools;

import com.tomcvt.pixelmate.dto.ParamInput;
import com.tomcvt.pixelmate.model.SimpleImageFrame;
import com.tomcvt.pixelmate.model.operations.KMeansOperation;
import com.tomcvt.pixelmate.parameters.KMeansParams;
import com.tomcvt.pixelmate.utility.ImageSaver;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class KMeansManager {
    private static final String ORIGINAL_NAME = "original_kmeans";
    private static final String KMEANS_NAME = "kmeans_result";
    private String sessionId;
    private String cacheDir;
    private String savedOriginalRelPath;
    private String savedKMeansRelPath;
    private KMeansOperation kMeansOperation = new KMeansOperation();
    private KMeansParams kMeansParams;
    private List<String> urlList = new ArrayList<>();
    //private SimpleImageFrame imageFrame;
    public KMeansManager(BufferedImage original, String sessionId, String cacheDir) {
        this.sessionId = sessionId;
        this.cacheDir = cacheDir;
        this.savedOriginalRelPath = ImageSaver.saveImage(cacheDir, sessionId, ORIGINAL_NAME, original);
        this.kMeansParams = KMeansParams.FACTORY.fromMap(null);
        urlList.add("/generated/" + savedOriginalRelPath);
    }

    public void run() {
        BufferedImage original = ImageSaver.loadImage(cacheDir, sessionId, ORIGINAL_NAME);
        SimpleImageFrame imgFrame = SimpleImageFrame.fromBufferedImage(original);
        SimpleImageFrame result = kMeansOperation.applySimple(imgFrame, kMeansParams);
        BufferedImage resultImage = result.getColoredImage();
        String savedRelPath = ImageSaver.saveImage(cacheDir, sessionId, KMEANS_NAME, resultImage);
        this.savedKMeansRelPath = savedRelPath;
        if (urlList.size() == 1) {
            urlList.add("/generated/" + savedKMeansRelPath);
        } else {
            urlList.set(1, "/generated/" + savedKMeansRelPath);
        }
    }
    public List<String> getUrlList() {
        return urlList;
    }
    public void updateParams(KMeansParams params) {
        this.kMeansParams = params;
    }

    public void updateParams(ParamInput paramInput) {
        KMeansParams params = KMeansParams.fromMapWithOldParams(kMeansParams, paramInput.values());
        this.kMeansParams = params;
    }
}
