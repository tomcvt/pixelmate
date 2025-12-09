package com.tomcvt.pixelmate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private final String cachedDir;

    public MvcConfig(@Value("${pixelmate.cache-dir}") String cachedDir) {
        this.cachedDir = cachedDir;
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/generated/**")
                .addResourceLocations("file:" + cachedDir + "/");
        
    }
}
