package com.tomcvt.pixelmate.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PipelineController {
    @GetMapping("/editor")
    public String editPictureWithPipeline() {
        return "pipeline/editor";
    }
}
