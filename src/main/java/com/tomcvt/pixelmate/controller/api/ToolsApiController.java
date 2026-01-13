package com.tomcvt.pixelmate.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.pixelmate.dto.OperationInfoDto;
import com.tomcvt.pixelmate.dto.ParamInput;
import com.tomcvt.pixelmate.dto.UrlsAndPalette;
import com.tomcvt.pixelmate.session.ToolsManager;

@RestController
@RequestMapping("/api/session/tools")
public class ToolsApiController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ToolsApiController.class);
    private final ToolsManager toolsManager;

    public ToolsApiController(ToolsManager toolsManager) {
        this.toolsManager = toolsManager;
    }

    @PostMapping("/kmeans/create-default")
    public ResponseEntity<?> createDefaultKmeans(@RequestParam("file") MultipartFile uploadImage) {
        toolsManager.createDefaultKmeans(uploadImage);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/kmeans/run")
    public ResponseEntity<UrlsAndPalette> runKMeans() {
        var urls = toolsManager.runKMeans();
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/kmeans/info")
    public ResponseEntity<OperationInfoDto> getKMeansInfo() {
        var info = toolsManager.getKMeansOperationInfo();
        return ResponseEntity.ok(info);
    }

    @PostMapping("/kmeans/update-param")
    public ResponseEntity<UrlsAndPalette> updateOperationParam(@RequestBody ParamInput paramInput) {
        var urls = toolsManager.updateKMeansParamsAndRun(paramInput);
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/kmeans/last-palette")
    public ResponseEntity<List<String>> getKMeansLastPalette() {
        var intPalette = toolsManager.getKMeansLastPalette();
        List<String> palette = intPalette.stream()
                .map(rgb -> String.format("#%06X", (0xFFFFFF & rgb)))
                .toList();
        return ResponseEntity.ok(palette);
    }

    @PostMapping("/kmeans/reset")
    public ResponseEntity<Void> resetPipeline() {
        toolsManager.clearKMeans();
        ResponseEntity<Void> response = ResponseEntity.status(302).header("Location", "/upload").build();
        log.info(response.toString());
        return response;
    }
    
}
