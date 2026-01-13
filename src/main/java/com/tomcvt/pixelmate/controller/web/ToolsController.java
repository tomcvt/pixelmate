package com.tomcvt.pixelmate.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tools")
public class ToolsController {
    @GetMapping("/palette-picker")
    public String showPalettePickerTool() {
        return "tools/k-means";
    }
    @GetMapping("/upload-tools")
    public String showUploadToolsPage() {
        return "tools/upload-tools";
    }

}
