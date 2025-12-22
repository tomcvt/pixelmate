package com.tomcvt.pixelmate.dto;

import java.util.Map;

public record DashboardInfoDto(
    Map<String, String> bannedIPs,
    Map<String, String> registeredIPs,
    Map<String, PipelineMetadataDto> pipelineInfo,
    String memoryUsageInfo
) {
    
}
