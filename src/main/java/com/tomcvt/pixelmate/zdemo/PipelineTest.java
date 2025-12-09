package com.tomcvt.pixelmate.zdemo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "demo"})
public class PipelineTest {
    
}
