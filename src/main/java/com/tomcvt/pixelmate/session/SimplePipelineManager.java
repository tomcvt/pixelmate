package com.tomcvt.pixelmate.session;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.pixelmate.pipeline.PipelineBuilder;
import com.tomcvt.pixelmate.pipeline.PipelineMetadata;
import com.tomcvt.pixelmate.pipeline.SimpleOperationsPipeline;
import com.tomcvt.pixelmate.registry.PipelineInfoRegistry;
import com.tomcvt.pixelmate.service.SessionCleanupService;
import com.tomcvt.pixelmate.dto.OperationInfoDto;
import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.operations.*;
import com.tomcvt.pixelmate.utility.ImageReader;

import jakarta.servlet.http.HttpSession;

@SessionScope
@Component
public class SimplePipelineManager {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SimplePipelineManager.class);
    private final PipelineInfoRegistry pipelineInfoRegistry;
    private final SessionCleanupService sessionCleanupService;
    private final long maxMemoryUsageBytes;
    private final int maxHeight;
    private final int maxWidth;
    private final long maxPixels;
    private SimpleOperationsPipeline pipeline;
    private boolean firstRunDone = false;
    private String sessionId;
    private String cacheDir;

    public SimplePipelineManager(@Value("${pixelmate.cache-dir}") String cacheDir, 
            @Value("${pixelmate.limits.memory-usage-mb}") long maxMemoryUsageMb,
            @Value("${pixelmate.img-constraints.max-height}") int maxHeight,
            @Value("${pixelmate.img-constraints.max-width}") int maxWidth,
            @Value("${pixelmate.img-constraints.max-pixels}") long maxPixels,
            HttpSession httpSession, 
            PipelineInfoRegistry pipelineInfoRegistry,
            SessionCleanupService sessionCleanupService) {
        this.cacheDir = cacheDir;
        this.maxMemoryUsageBytes = maxMemoryUsageMb * 1024 * 1024;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.maxPixels = maxPixels;
        this.sessionId = httpSession.getId();
        this.pipelineInfoRegistry = pipelineInfoRegistry;
        this.sessionCleanupService = sessionCleanupService;
    }
    //TODO add method to create pipeline with stock image

    public void createDefaultPipeline(MultipartFile uploadImage) {
        BufferedImage image = ImageReader.loadImage(uploadImage);
        int width = image.getWidth();
        int height = image.getHeight();
        checkImageConstraints(width, height);
        long estimatedImageSize = (long) width * height * 4; // Approximate size in bytes (ARGB)
        long estimatedRunningMemory = estimatedImageSize * 4; // 3 processing overhead * 1.3 safety margin 
        double estimatedRunningMemoryMB = estimatedRunningMemory / (1024.0 * 1024.0);
        log.info("Estimated memory usage for processing image: {} x {} = {:.2f} MB", width, height, estimatedRunningMemoryMB);
        long currentMemUsage = pipelineInfoRegistry.getTotalEstimatedMemoryUsageBytes();
        if (currentMemUsage + estimatedRunningMemory > maxMemoryUsageBytes) {
            throw new IllegalStateException("Cannot create pipeline: estimated memory usage exceeds limit, try again later or with smaller image. MB left:" 
                    + ((maxMemoryUsageBytes - currentMemUsage) / (1024.0 * 1024.0)));
        }
        if (this.pipeline != null) {
            // Clear previous session cache
            sessionCleanupService.clearSessionDiskCache(this.sessionId);
        }
        var metadata = new PipelineMetadata(sessionId, estimatedRunningMemory, Instant.now());
        pipelineInfoRegistry.registerPipelineInfo(sessionId, metadata);
        //log.info("Estimated memory usage for processing image: {} x {} {} bytes", width, height, estimatedRunningMemory);
        this.pipeline = new SimpleOperationsPipeline(image, sessionId, cacheDir);
        //TODO here change to dynamic reflection from map parameters constructor
        this.pipeline = PipelineBuilder.builder()
                .add(new KMeansOperation(), KMeansOperation.createDefaultPipelineParams())
                .add(new NearNeigbourRescale(), NearNeigbourRescale.createDefaultPipelineParams())
                .add(new EdgeDetectionOperation(), EdgeDetectionOperation.createDefaultPipelineParams())
                .add(new ThickenEdgesOperation(), ThickenEdgesOperation.createDefaultPipelineParams())
                .add(new EdgeQuantizationOperation(), EdgeQuantizationOperation.createDefaultPipelineParams())
                .add(new ApplyEdgesOperation(), ApplyEdgesOperation.createDefaultPipelineParams())
                .buildSimpleNodes(this.pipeline);
    }

    public SimpleOperationsPipeline getPipeline() {
        if (pipeline == null) {
            throw new IllegalStateException("Pipeline has not been created yet.");
        }
        return pipeline;
    }

    public List<String> getOperationNames() {
        return getPipeline().getOperationNames();
    }

    public List<String> getUrlList() {
        return getPipeline().getUrlList();
    }

    public List<String> runPipeline() {
        if (firstRunDone) {
            //getPipeline().clearUrls();
            getPipeline().run();
        } else {
            firstRunDone = true;
            getPipeline().firstRun();
        }
        return getPipeline().getUrlList();
    }

    public List<String> runFromIndex(int index) {
        getPipeline().run(index);
        return getPipeline().getUrlList();
    }

    public List<String> updateOperationParamsAndRun(int index, Map<String, Object> values) {
        getPipeline().updateNodeParameters(index, values);
        getPipeline().run(index);
        return getPipeline().getUrlList();
    }

    public List<List<ParamSpec>> getOperationsParamSpecs() {
        return getPipeline().getOperationsParamSpecs();
    }

    public List<OperationInfoDto> getOperationsInfo() {
        return getPipeline().getOperationsInfo();
    }

    private void checkImageConstraints(int width, int height) {
        if (width > maxWidth) {
            throw new IllegalArgumentException("Image width exceeds maximum allowed: " + maxWidth);
        }
        if (height > maxHeight) {
            throw new IllegalArgumentException("Image height exceeds maximum allowed: " + maxHeight);
        }
        long totalPixels = (long) width * height;
        if (totalPixels > maxPixels) {
            throw new IllegalArgumentException("Image total pixels exceed maximum allowed: " + maxPixels);
        }
    }
}
