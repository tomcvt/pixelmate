package com.tomcvt.pixelmate.session;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate.Param;
import com.tomcvt.pixelmate.dto.OperationInfoDto;
import com.tomcvt.pixelmate.dto.ParamInput;
import com.tomcvt.pixelmate.model.operations.ApplyEdgesOperation;
import com.tomcvt.pixelmate.model.operations.EdgeDetectionOperation;
import com.tomcvt.pixelmate.model.operations.EdgeQuantizationOperation;
import com.tomcvt.pixelmate.model.operations.KMeansOperation;
import com.tomcvt.pixelmate.model.operations.NearNeigbourRescale;
import com.tomcvt.pixelmate.model.operations.ThickenEdgesOperation;
import com.tomcvt.pixelmate.parameters.KMeansParams;
import com.tomcvt.pixelmate.pipeline.PipelineBuilder;
import com.tomcvt.pixelmate.pipeline.PipelineMetadata;
import com.tomcvt.pixelmate.pipeline.SimpleOperationsPipeline;
import com.tomcvt.pixelmate.registry.PipelineInfoRegistry;
import com.tomcvt.pixelmate.service.SessionCleanupService;
import com.tomcvt.pixelmate.tools.KMeansManager;
import com.tomcvt.pixelmate.utility.ImageReader;

import jakarta.servlet.http.HttpSession;

@SessionScope
@Component
public class ToolsManager {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ToolsManager.class);
    private String cacheDir;
    private long maxMemoryUsageBytes;
    private int maxHeight;
    private int maxWidth;
    private long maxPixels;
    private String sessionId;
    private SessionCleanupService sessionCleanupService;
    // TODO add registry for mem usage tracking
    private KMeansManager kMeansManager;

    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final BlockingQueue<ParamInput> updateQueue = new LinkedBlockingQueue<>();
    private final Object monitor = new Object();
    private volatile boolean workerRunning = false;

    public ToolsManager(@Value("${pixelmate.cache-dir}") String cacheDir,
            @Value("${pixelmate.limits.memory-usage-mb}") long maxMemoryUsageMb,
            @Value("${pixelmate.img-constraints.max-height}") int maxHeight,
            @Value("${pixelmate.img-constraints.max-width}") int maxWidth,
            @Value("${pixelmate.img-constraints.max-pixels}") long maxPixels,
            HttpSession httpSession,
            SessionCleanupService sessionCleanupService) {
        this.cacheDir = cacheDir;
        this.maxMemoryUsageBytes = maxMemoryUsageMb * 1024 * 1024;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.maxPixels = maxPixels;
        this.sessionId = httpSession.getId();
        this.sessionCleanupService = sessionCleanupService;
    }

    public synchronized void createDefaultKmeans(MultipartFile uploadImage) {
        BufferedImage image = ImageReader.loadImage(uploadImage);
        int width = image.getWidth();
        int height = image.getHeight();
        checkImageConstraints(width, height);
        long estimatedImageSize = (long) width * height * 4; // Approximate size in bytes (ARGB)
        long estimatedRunningMemory = estimatedImageSize * 4; // 3 processing overhead * 1.3 safety margin
        double estimatedRunningMemoryMB = estimatedRunningMemory / (1024.0 * 1024.0);
        String estimatedMB = String.format("%.2f", estimatedRunningMemoryMB);
        log.info("Estimated memory usage for processing image: {} x {} = {} MB", width, height, estimatedMB);
        if (this.kMeansManager != null) {
            cleanupSession();
            log.info("Cleaning up previous KMeansManager for session {}", sessionId);
            this.kMeansManager = null;
        }
        this.kMeansManager = new KMeansManager(image, sessionId, cacheDir);
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

    public KMeansManager getKMeansManager() {
        if (kMeansManager == null) {
            throw new IllegalStateException("KMeansManager not initialized. Call createDefaultPipeline first.");
        }
        return kMeansManager;
    }

    public void clearKMeans() {
        if (this.kMeansManager != null) {
            cleanupSession();
            log.info("Clearing KMeansManager for session {}", sessionId);
            this.kMeansManager = null;
        }
    }

    public OperationInfoDto getKMeansOperationInfo() {
        return new OperationInfoDto(
                "KMEANS",
                KMeansParams.getParamSpecs());
    }

    public List<String> getKMeansUrls() {
        return getKMeansManager().getUrlList();
    }

    // TODO implement selective cleanup
    public void cleanupSession() {
        sessionCleanupService.clearSessionDiskCache(sessionId);
    }

    public List<String> runKMeans() {
        KMeansManager manager = getKMeansManager();
        manager.run();
        return manager.getUrlList();
    }

    public List<String> updateKMeansParamsAndRun(ParamInput paramInput) {
        updateQueue.offer(paramInput);
        startWorkerIfNeeded();
        waitUntilQueueEmptyAndWorkerIdle();
        return getKMeansManager().getUrlList();
    }

    private void startWorkerIfNeeded() {
        synchronized (monitor) {
            if (!workerRunning) {
                workerRunning = true;
                worker.submit(this::processUpdateQueue);
            }
        }
    }

    private void processUpdateQueue() {
        try {
            while (true) {
                ParamInput paramInput = updateQueue.poll();
                if (paramInput == null) {
                    break;
                }
                List<ParamInput> toProcess = new java.util.ArrayList<>();
                toProcess.add(paramInput);
                updateQueue.drainTo(toProcess);
                applyUpdates(toProcess);
                getKMeansManager().run();
            }
        } finally {
            synchronized (monitor) {
                workerRunning = false;
                monitor.notifyAll();
            }
        }
    }

    private void waitUntilQueueEmptyAndWorkerIdle() {
        synchronized (monitor) {
            while (!updateQueue.isEmpty() || workerRunning) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void applyUpdates(List<ParamInput> updates) {
        KMeansManager manager = getKMeansManager();
        for (ParamInput input : updates) {
            manager.updateParams(input);
        }
    }

}
