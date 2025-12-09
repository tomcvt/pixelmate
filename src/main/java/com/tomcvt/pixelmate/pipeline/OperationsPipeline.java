package com.tomcvt.pixelmate.pipeline;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.utility.ImageSaver;

public class OperationsPipeline {
    private PipelineNode<?> head;
    private PipelineNode<?> tail;
    private List<PipelineNode<?>> nodes = new ArrayList<>();
    // private Map<String, Integer> nameMap = new HashMap<>();
    private List<String> urlList = new ArrayList<>();
    private List<ImageFrame> resultCache = new ArrayList<>(); // index 0 original with saved name, i + 1 index of i node
    private UUID tempName = UUID.randomUUID();
    private String sessionId;
    private String cacheDir;
    private String savedOriginalRelPath;
    // private ImageSaver imageSaver = new ImageSaver();
    // TODO later interface for different storage types
    private BufferedImage original;
    private MultipartFile uploadFile;

    public OperationsPipeline(BufferedImage original, String sessionId, String cacheDir) {
        this.original = original;
        this.sessionId = sessionId;
        this.cacheDir = cacheDir;
        this.savedOriginalRelPath = ImageSaver.saveImage(cacheDir, sessionId, "original", original);
        resultCache.add(ImageFrame.fromBufferedImage(original));
        urlList.add("/generated/" + savedOriginalRelPath);
    }

    public OperationsPipeline(PipelineNode<?> head, PipelineNode<?> tail, List<PipelineNode<?>> nodes) {
        this.head = head;
        this.tail = tail;
        this.nodes = nodes;
    }

    public void clearCacheAndUrls() {
        this.urlList.clear();
        this.resultCache.clear();
        resultCache.add(ImageFrame.fromBufferedImage(original));
        urlList.add("/generated/" + savedOriginalRelPath);
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setTail(PipelineNode<?> tail) {
        this.tail = tail;
    }

    public void setHead(PipelineNode<?> head) {
        this.head = head;
    }

    public void setNodes(List<PipelineNode<?>> nodes) {
        this.nodes = nodes;
    }

    public List<PipelineNode<?>> getNodes() {
        return nodes;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public List<String> getOperationNames() {
        return nodes.stream().map(n -> n.getOperation().getName()).toList();
    }

    public void run() {
        run(resultCache.get(0), 0);
    }

    public void run(ImageFrame inputImage) {
        run(inputImage, 0);
    }

    public void run(int startIndex) {
        if (startIndex < 0 || startIndex >= nodes.size()) {
            throw new IllegalArgumentException("Invalid start index: " + startIndex);
        }
        if (startIndex > resultCache.size() - 1)
            throw new IllegalArgumentException("Pipeline has not been run up to index " + startIndex);
        run(ImageFrame.fromBufferedImage(original), startIndex);
    }

    public void run(ImageFrame inputImage, int startIndex) {
        ImageFrame currentImage = inputImage;
        for (int i = startIndex; i < nodes.size(); i++) {
            PipelineNode<?> node = nodes.get(i);

            currentImage = node.process(currentImage);
            if (currentImage == null) {
                throw new RuntimeException("Operation " + node.getOperation().getName() + " returned null image.");
            }
            BufferedImage bufferedResult = currentImage.getBufferedImage(currentImage.getLastResult());
            String relPath = ImageSaver.saveImage(cacheDir, sessionId, "result_" + i, bufferedResult);
            urlList.add("/generated/" + relPath);
            resultCache.add(currentImage);
        }
    }
}
