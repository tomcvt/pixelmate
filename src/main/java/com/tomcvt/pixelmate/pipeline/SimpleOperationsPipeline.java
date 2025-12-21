package com.tomcvt.pixelmate.pipeline;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.OperationInfoDto;
import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.SimpleImageFrame;
import com.tomcvt.pixelmate.utility.ImageSaver;

public class SimpleOperationsPipeline {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SimpleOperationsPipeline.class);
    private List<PipelineNode<?>> nodes = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();
    private List<String> imageNames = new ArrayList<>();
    private String sessionId;
    private String cacheDir;
    private List<Integer> lastColoredIndex = new ArrayList<>();
    private Integer lastColorContext = null;
    private String savedOriginalRelPath;

    public SimpleOperationsPipeline(BufferedImage original, String sessionId, String cacheDir) {
        this.sessionId = sessionId;
        this.cacheDir = cacheDir;
        this.savedOriginalRelPath = ImageSaver.saveImage(cacheDir, sessionId, "original", original);
        this.imageNames.add("original");
        urlList.add("/generated/" + savedOriginalRelPath);
    }

    public SimpleOperationsPipeline(List<PipelineNode<?>> nodes) {
        this.nodes = nodes;
    }

    public void clearCacheAndUrls() {
        this.urlList.clear();
        this.imageNames.clear();
        this.imageNames.add("original");
        this.urlList.add("/generated/" + savedOriginalRelPath);
    }

    public void clearUrls() {
        this.urlList.clear();
        this.urlList.add("/generated/" + savedOriginalRelPath);
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
        run(
                SimpleImageFrame.fromBufferedImage(
                        ImageSaver.loadImage(cacheDir, sessionId, "original")),
                0);
    }

    public void run(SimpleImageFrame inputImage) {
        run(inputImage, 0);
    }

    public void run(int startIndex) {
        if (startIndex < 0 || startIndex >= nodes.size()) {
            throw new IndexOutOfBoundsException("Invalid start index: " + startIndex);
        }
        if (startIndex > imageNames.size() - 1)
            throw new IndexOutOfBoundsException("Pipeline has not been run up to index " + startIndex);
        run(null, startIndex);
    }

    public void firstRun() {
        SimpleImageFrame currentImage = SimpleImageFrame.fromBufferedImage(
                ImageSaver.loadImage(cacheDir, sessionId, "original"));
        lastColorContext = -1; // original image is color context
        for (int i = 0; i < nodes.size(); i++) {
            PipelineNode<?> node = nodes.get(i);
            // Special case: if the operation is EDGE_DETECTION, use the original image as
            // input
            if (node.getOperation().getName().equals("EDGE_DETECTION")) {
                currentImage = currentImage.withEdge(
                        ImageSaver.loadImage(cacheDir, sessionId, "original"));
            }
            currentImage = node.process(currentImage);
            if (currentImage == null) {
                throw new RuntimeException("Operation " + node.getOperation().getName() + " returned null image.");
            }
            lastColoredIndex.add(lastColorContext);
            switch (node.getOperation().getOperationType()) {
                case COLOR -> {
                    lastColorContext = i;
                }
                default -> {
                }
            }
            BufferedImage bufferedResult = currentImage.getCurrentImage();
            String relPath = ImageSaver.saveImage(cacheDir, sessionId, "result_" + i, bufferedResult);
            imageNames.add("result_" + i);
            urlList.add("/generated/" + relPath);
        }
    }

    public void run(SimpleImageFrame inputImage, int startIndex) {
        // null inputImage means use cached result
        if (inputImage == null) {
            String fromImageName = imageNames.get(startIndex);
            inputImage = SimpleImageFrame.fromBufferedImage(
                    ImageSaver.loadImage(cacheDir, sessionId, fromImageName));
        }
        SimpleImageFrame currentImage = inputImage;
        var firstNode = nodes.get(startIndex);
        // determine color context for first node
        switch (firstNode.getOperation().getOperationType()) {
            case EDGE -> {
                int lastColoredOpIndex = lastColoredIndex.get(startIndex); // for operation at startIndex, get its last
                                                                           // colored op index
                String name;
                if (lastColoredOpIndex == -1) {
                    name = "original";
                } else {
                    name = imageNames.get(lastColoredOpIndex + 1); // +1 because original is at index 0
                }
                currentImage = currentImage.withColored(
                        ImageSaver.loadImage(
                                cacheDir,
                                sessionId,
                                name // +1 because original is at index 0
                        ));
            }
            default -> {
            }
        }
        for (int i = startIndex; i < nodes.size(); i++) {
            PipelineNode<?> node = nodes.get(i);
            if (node.getOperation().getName().equals("EDGE_DETECTION")) {
                // log.info("--------DEBUG: Using original image for EDGE_DETECTION operation");
                currentImage = currentImage.withEdge(
                        ImageSaver.loadImage(cacheDir, sessionId, "original"));
            }
            currentImage = node.process(currentImage);
            BufferedImage bufferedResult = currentImage.getCurrentImage();
            String relPath = ImageSaver.saveImage(cacheDir, sessionId, "result_" + i, bufferedResult);
            imageNames.set(i + 1, "result_" + i);
            urlList.set(i + 1, "/generated/" + relPath);
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
        List<List<ParamSpec>> specs = new ArrayList<>();
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
                    node.getOperation().getParamSpecs()));
        }
        return infos;
    }

    private void clearIntermediateResultsFrom(int index) {
        while (urlList.size() > index + 1) {
            urlList.remove(urlList.size() - 1);
        }
    }

    // TODO implement bfs on edge thickening to optimize
    // TODO add flag for edge skipping

}
