package com.tomcvt.pixelmate.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.pixelmate.dto.OperationInfoDto;
import com.tomcvt.pixelmate.dto.ParamInput;
import com.tomcvt.pixelmate.session.SimplePipelineManager;

import io.micrometer.core.ipc.http.HttpSender.Response;

@RestController
@RequestMapping("/api/session/pipeline")
public class SimplePipelineApiController {
    private final SimplePipelineManager pipelineManager;

    public SimplePipelineApiController(SimplePipelineManager pipelineManager) {
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
    @GetMapping("/operations/info")
    public ResponseEntity<List<OperationInfoDto>> getOperationsInfo() {
        var infos = pipelineManager.getOperationsInfo();
        return ResponseEntity.ok(infos);
    }

    @PostMapping("/operations/update-param")
    public ResponseEntity<List<String>> updateOperationParam(@RequestBody ParamInput paramInput) {
        var urls = pipelineManager.updateOperationParamsAndRun(paramInput);
        return ResponseEntity.ok(urls);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPipeline() {
        pipelineManager.clearPipeline();
        var response = ResponseEntity.status(302).header("Location", "/upload");
        return response.build();
    }
}