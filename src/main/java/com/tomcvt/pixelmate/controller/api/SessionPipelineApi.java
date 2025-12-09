package com.tomcvt.pixelmate.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.pixelmate.session.PipelineManager;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/session/pipeline")
public class SessionPipelineApi {
    private final PipelineManager pipelineManager;

    public SessionPipelineApi(PipelineManager pipelineManager) {
        this.pipelineManager = pipelineManager;
    }

    @PostMapping("/create-default")
    public ResponseEntity<?> createDefaultPipeline(@RequestParam("file") MultipartFile uploadImage) {
        pipelineManager.createDefaultPipeline(uploadImage);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/run")
    public ResponseEntity<List<String>> runPipeline() {
        var urls = pipelineManager.runPipeline();
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/operations/names")
    public ResponseEntity<List<String>> getOperationNames() {
        var names = pipelineManager.getOperationNames();
        return ResponseEntity.ok(names);
    }
    
}
