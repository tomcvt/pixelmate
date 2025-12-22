package com.tomcvt.pixelmate.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.tomcvt.pixelmate.dto.DashboardInfoDto;
import com.tomcvt.pixelmate.dto.PipelineMetadataDto;
import com.tomcvt.pixelmate.network.BanRegistry;
import com.tomcvt.pixelmate.network.IpRegistry;
import com.tomcvt.pixelmate.registry.PipelineInfoRegistry;

@Service
public class AdminService {
    private final BanRegistry banRegistry;
    private final PipelineInfoRegistry pipelineInfoRegistry;
    private final IpRegistry ipRegistry;

    public AdminService(BanRegistry banRegistry, PipelineInfoRegistry pipelineInfoRegistry, IpRegistry ipRegistry) {
        this.banRegistry = banRegistry;
        this.pipelineInfoRegistry = pipelineInfoRegistry;
        this.ipRegistry = ipRegistry;
    }

    public Map<String, PipelineMetadataDto> getAllPipelineInfo() {
        var map = pipelineInfoRegistry.getAllPipelineInfo();
        return map.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                e -> new PipelineMetadataDto(
                    e.getKey(),
                    String.format("%.2f MB", e.getValue().estimatedMemoryUsageBytes() / (1024.0 * 1024.0)),
                    e.getValue().creationTime().toString())
                )
            );
    }

    public String getMemoryUsageInfo() {
        return pipelineInfoRegistry.getMemoryUsageInfo();
    }

    public Map<String, String> getAllBannedIPs() {
        return banRegistry.getBannedIPs().entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                e -> java.time.Instant.ofEpochMilli(e.getValue()).toString()
            ));
    }

    public Map<String, String> getRegisteredIPs() {
        return ipRegistry.getIpRequestCounters().entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().toString()
            ));
    }

    public DashboardInfoDto getDashboardInfo() {
        return new DashboardInfoDto(
            getAllBannedIPs(),
            getRegisteredIPs(),
            getAllPipelineInfo(),
            getMemoryUsageInfo()
        );
    }
}
