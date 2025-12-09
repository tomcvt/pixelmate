package com.tomcvt.pixelmate.session;

import java.awt.image.BufferedImage;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.pixelmate.model.EdgeDetectionOperation;
import com.tomcvt.pixelmate.model.ThickenEdgesOperation;
import com.tomcvt.pixelmate.pipeline.OperationsPipeline;
import com.tomcvt.pixelmate.pipeline.PipelineBuilder;
import com.tomcvt.pixelmate.utility.ImageReader;

import jakarta.servlet.http.HttpSession;

@SessionScope
@Component
public class PipelineManager {
    private OperationsPipeline pipeline;
    private String sessionId;
    private String cacheDir;

    
    public PipelineManager(@Value("${pixelmate.cache-dir}") String cacheDir, HttpSession httpSession) {
        this.cacheDir = cacheDir;
        this.sessionId = httpSession.getId();
    }

    public void createDefaultPipeline(MultipartFile uploadImage) {
        BufferedImage image = ImageReader.loadImage(uploadImage);
        if (image == null) {
            System.out.println("Loaded image for session " + sessionId + ": " + image);
            throw new RuntimeException("Failed to load image from upload");
        }
        this.pipeline = new OperationsPipeline(image, sessionId, cacheDir);
        //TODO here change to dynamic reflection from map parameters constructor
        this.pipeline = PipelineBuilder.builder()
                .add(new EdgeDetectionOperation(), EdgeDetectionOperation.createDefaultParams())
                .add(new ThickenEdgesOperation(), ThickenEdgesOperation.createDefaultParams())
                .buildNodes(this.pipeline);
    }

    public OperationsPipeline getPipeline() {
        return pipeline;
    }

    public List<String> getOperationNames() {
        return pipeline.getOperationNames();
    }

    public List<String> getUrlList() {
        return pipeline.getUrlList();
    }

    public List<String> runPipeline() {
        pipeline.run();
        return pipeline.getUrlList();
    }

    public List<String> runFromIndex(int index) {
        pipeline.run(index);
        return pipeline.getUrlList();
    }
}
