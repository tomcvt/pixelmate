package com.tomcvt.pixelmate.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.pixelmate.dto.OperationInfoDto;
import com.tomcvt.pixelmate.dto.ParamInput;
import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.session.PipelineManager;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/extended/session/pipeline")
public class SessionPipelineApiController {
    private final PipelineManager pipelineManager;

    public SessionPipelineApiController(PipelineManager pipelineManager) {
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
    //redundant with above
    @GetMapping("/operations/names")
    public ResponseEntity<List<String>> getOperationNames() {
        var names = pipelineManager.getOperationNames();
        return ResponseEntity.ok(names);
    }
    //redundant with above
    @GetMapping("/operations/paramspecs")
    public ResponseEntity<List<List<ParamSpec>>> getOperationParamSpecs() {
        var specs = pipelineManager.getOperationsParamSpecs();
        return ResponseEntity.ok(specs);
    }

    @PostMapping("/operations/update-param")
    public ResponseEntity<List<String>> updateOperationParam(@RequestBody ParamInput paramInput) {
        var urls = pipelineManager.updateOperationParamsAndRun(paramInput.index(), paramInput.values());
        return ResponseEntity.ok(urls);
    }


    
}
