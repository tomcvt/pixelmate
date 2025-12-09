package com.tomcvt.pixelmate.pipeline;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.OperationInfoDto;
import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.utility.ImageSaver;

public class OperationsPipeline {
    private List<PipelineNode<?>> nodes = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();
    private List<ImageFrame> resultCache = new ArrayList<>(); // index 0 original with saved name, i + 1 index of i node
    private String sessionId;
    private String cacheDir;
    private String savedOriginalRelPath;
    private BufferedImage original;

    public OperationsPipeline(BufferedImage original, String sessionId, String cacheDir) {
        this.original = original;
        this.sessionId = sessionId;
        this.cacheDir = cacheDir;
        this.savedOriginalRelPath = ImageSaver.saveImage(cacheDir, sessionId, "original", original);
        resultCache.add(ImageFrame.fromBufferedImage(original));
        urlList.add("/generated/" + savedOriginalRelPath + getTimeStampString());
    }

    public OperationsPipeline(List<PipelineNode<?>> nodes) {
        this.nodes = nodes;
    }

    public void clearCacheAndUrls() {
        this.urlList.clear();
        this.resultCache.clear();
        resultCache.add(ImageFrame.fromBufferedImage(original));
        urlList.add("/generated/" + savedOriginalRelPath + getTimeStampString());
    }

    public void clearUrls() {
        this.urlList.clear();
        urlList.add("/generated/" + savedOriginalRelPath + getTimeStampString());
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
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
        clearIntermediateResultsFrom(startIndex);
        for (int i = startIndex; i < nodes.size(); i++) {
            PipelineNode<?> node = nodes.get(i);

            currentImage = node.process(currentImage);
            if (currentImage == null) {
                throw new RuntimeException("Operation " + node.getOperation().getName() + " returned null image.");
            }
            BufferedImage bufferedResult = currentImage.getBufferedImage(currentImage.getLastResult());
            String relPath = ImageSaver.saveImage(cacheDir, sessionId, "result_" + i, bufferedResult);
            urlList.add("/generated/" + relPath + getTimeStampString());
            resultCache.add(currentImage);
        }
    }

    public void updateNodeParameters(int index, Map<String, Object> values) {
        if (index < 0 || index >= nodes.size()) {
            throw new IllegalArgumentException("Invalid node index: " + index);
        }
        PipelineNode<?> node = nodes.get(index);
        node.updateParameters(values);
    }

    public List<List<ParamSpec>> getOperationsParamSpecs() {
        List<List<com.tomcvt.pixelmate.dto.ParamSpec>> specs = new ArrayList<>();
        for (PipelineNode<?> node : nodes) {
            specs.add(node.getOperation().getParamSpecs());
        }
        return specs;
    }

    public List<OperationInfoDto> getOperationsInfo() {
        List<OperationInfoDto> infos = new ArrayList<>();
        for (PipelineNode<?> node : nodes) {
            infos.add(new OperationInfoDto(
                node.getOperation().getName(),
                node.getOperation().getParamSpecs()
            ));
        }
        return infos;
    }

    private String getTimeStampString() {
        return "?t=" + System.currentTimeMillis();
    }

    private void clearIntermediateResultsFrom(int index) {
        while (resultCache.size() > index + 1) {
            resultCache.remove(resultCache.size() - 1);
        }
        while (urlList.size() > index + 1) {
            urlList.remove(urlList.size() - 1);
        }
    }
}
